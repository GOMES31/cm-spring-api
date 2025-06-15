package dev.edugomes.springapi.service.team;

import dev.edugomes.springapi.domain.Team;
import dev.edugomes.springapi.domain.TeamMember;
import dev.edugomes.springapi.domain.TeamRole;
import dev.edugomes.springapi.domain.User;
import dev.edugomes.springapi.dto.request.AddTeamMemberRequest;
import dev.edugomes.springapi.dto.request.CreateTeamRequest;
import dev.edugomes.springapi.dto.request.UpdateTeamMemberRequest;
import dev.edugomes.springapi.dto.request.UpdateTeamRequest;
import dev.edugomes.springapi.dto.response.TeamMemberResponse;
import dev.edugomes.springapi.dto.response.TeamResponse;
import dev.edugomes.springapi.exception.TeamNotFoundException;
import dev.edugomes.springapi.exception.UserNotFoundException;
import dev.edugomes.springapi.mapper.CustomMapper;
import dev.edugomes.springapi.repository.TeamMemberRepository;
import dev.edugomes.springapi.repository.TeamRepository;
import dev.edugomes.springapi.repository.UserRepository;
import dev.edugomes.springapi.service.log.LogService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import static dev.edugomes.springapi.utils.GlobalMethods.getCurrentUserEmail;


@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private static final Logger logger = LoggerFactory.getLogger(TeamServiceImpl.class);

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final LogService logService;


    @Override
    public TeamResponse create(CreateTeamRequest request) {
        String userEmail = getCurrentUserEmail();

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Team team = Team.builder()
                .name(request.getName())
                .department(request.getDepartment())
                .imageUrl(request.getImageUrl())
                .build();

        team = teamRepository.save(team);

        TeamMember teamMember = TeamMember.builder()
                .user(user)
                .team(team)
                .role(TeamRole.ADMIN)
                .build();

        teamMember = teamMemberRepository.save(teamMember);

        team.addMember(teamMember);
        user.addMembership(teamMember);

        team = teamRepository.save(team);

        String email = getCurrentUserEmail();

        logService.saveLog("Create Team",email);

        return CustomMapper.toTeamResponse(team);
    }

    @Override
    public TeamResponse getTeamById(Long teamId) {
        String userEmail = getCurrentUserEmail();

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new UserNotFoundException("Team not found"));

        if (team.getMembers().stream().noneMatch(member -> member.getUser().getId().equals(user.getId()))) {
            throw new UserNotFoundException("User is not a member of this team");
        }

        String email = getCurrentUserEmail();

        logService.saveLog("Get Team by ID", email);

        return CustomMapper.toTeamResponse(team);
    }


    @Override
    public TeamResponse updateTeam(Long teamId, UpdateTeamRequest request) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException("Team not found"));

        if(team.getUpdatedAt() != null) {
            if(request.getUpdatedAt() < team.getUpdatedAt().getTime()) {
                return CustomMapper.toTeamResponse(team);
            }
        }

        if (request.getName() != null && !request.getName().isEmpty()) {
            team.setName(request.getName());
        }

        if (request.getDepartment() != null && !request.getDepartment().isEmpty()) {
            team.setDepartment(request.getDepartment());
        }

        if (request.getImageUrl() != null && !request.getImageUrl().isEmpty()) {
            team.setImageUrl(request.getImageUrl());
        }

        team = teamRepository.save(team);

        String email = getCurrentUserEmail();
        logService.saveLog("Update Team Profile", email);

        return CustomMapper.toTeamResponse(team);
    }


    @Override
    public TeamMemberResponse addMember(Long teamId, AddTeamMemberRequest request) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException("Team not found"));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        boolean isAlreadyMember = team.getMembers().stream()
                .anyMatch(member -> member.getUser().getId().equals(user.getId()));

        if (isAlreadyMember) {
            throw new RuntimeException("User is already a member of this team");
        }

        TeamMember teamMember = TeamMember.builder()
                .user(user)
                .team(team)
                .role(TeamRole.valueOf(request.getRole()))
                .build();

        teamMember = teamMemberRepository.save(teamMember);

        team.addMember(teamMember);
        user.addMembership(teamMember);

        userRepository.save(user);
        teamRepository.save(team);

        String email = getCurrentUserEmail();
        logService.saveLog("Add Team Member", email);

        return CustomMapper.toTeamMemberResponse(teamMember);
    }

    @Override
    public void removeMember(Long teamId, Long memberId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamNotFoundException("Team not found"));

        TeamMember teamMember = teamMemberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Team member not found"));

        // Check if member belongs to the specified team
        if (!teamMember.getTeam().getId().equals(teamId)) {
            throw new RuntimeException("Member does not belong to the specified team");
        }

        User user = teamMember.getUser();

        // Remove associations
        team.removeMember(teamMember);
        user.removeMembership(teamMember);

        // Remove the team member
        teamMemberRepository.delete(teamMember);

        String email = getCurrentUserEmail();
        logService.saveLog("Remove Team Member", email);
    }



    @Override
    public TeamMemberResponse updateMember(Long teamId, Long memberId, UpdateTeamMemberRequest request) {


        TeamMember teamMember = teamMemberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("Team member not found"));

        if(teamMember.getUpdatedAt() != null) {
            if(request.getUpdatedAt() < teamMember.getUpdatedAt().getTime()) {
                return CustomMapper.toTeamMemberResponse(teamMember);
            }
        }

        if (!teamMember.getTeam().getId().equals(teamId)) {
            throw new RuntimeException("Member does not belong to the specified team");
        }

        if (request.getRole() != null && !request.getRole().isEmpty()) {
            teamMember.setRole(TeamRole.valueOf(request.getRole()));
        }

        teamMemberRepository.save(teamMember);

        String email = getCurrentUserEmail();
        logService.saveLog("Update Team Member Role", email);

        return CustomMapper.toTeamMemberResponse(teamMember);
    }

}
