package dev.edugomes.springapi.service.team;

import dev.edugomes.springapi.domain.Team;
import dev.edugomes.springapi.domain.TeamMember;
import dev.edugomes.springapi.domain.TeamRole;
import dev.edugomes.springapi.domain.User;
import dev.edugomes.springapi.dto.request.CreateTeamRequest;
import dev.edugomes.springapi.dto.response.TeamResponse;
import dev.edugomes.springapi.exception.UserNotFoundException;
import dev.edugomes.springapi.mapper.Mapper;
import dev.edugomes.springapi.repository.TeamMemberRepository;
import dev.edugomes.springapi.repository.TeamRepository;
import dev.edugomes.springapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static dev.edugomes.springapi.util.GlobalMethods.getCurrentUserEmail;


@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private static final Logger logger = LoggerFactory.getLogger(TeamServiceImpl.class);

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final TeamMemberRepository teamMemberRepository;


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

        return Mapper.toTeamResponse(team);
    }
}
