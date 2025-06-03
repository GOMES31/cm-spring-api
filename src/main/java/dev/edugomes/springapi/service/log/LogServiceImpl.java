package dev.edugomes.springapi.service.log;


import dev.edugomes.springapi.domain.Log;
import dev.edugomes.springapi.repository.LogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogServiceImpl implements LogService{

    private final LogRepository logRepository;

    @Override
    public void saveLog(String action, String email) {
        Log log = Log.builder()
                .action(action)
                .email(email)
                .build();

        logRepository.save(log);
    }
}
