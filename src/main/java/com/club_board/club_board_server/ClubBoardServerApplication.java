package com.club_board.club_board_server;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

import java.util.Date;
import java.util.TimeZone;

@Slf4j
@EnableAsync
@SpringBootApplication
public class ClubBoardServerApplication {


	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
		SpringApplication.run(ClubBoardServerApplication.class, args);
		TimeZone timeZone = TimeZone.getDefault();
		log.info("JVM TimeZone: {}", timeZone.getID()); // 결과가 'UTC'여야 함
		log.info("서버 현재 시간: {}", new Date());
	}

}
