package com.club_board.club_board_server.service.file;

import com.club_board.club_board_server.domain.file.File;
import com.club_board.club_board_server.repository.file.FileRepository;
import com.club_board.club_board_server.response.exception.BusinessException;
import com.club_board.club_board_server.response.exception.ExceptionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class FileService {
    private final FileRepository fileRepository;

    @Transactional
    public File saveFileName(String objectName) {
        return fileRepository.save(new File(objectName));
    }

    public String getFileName(Long fileId) {
        return fileRepository.findById(fileId)
                .orElseThrow(() -> new BusinessException(ExceptionType.FILE_NOT_FOUND))
                .getUrl();
    }
}
