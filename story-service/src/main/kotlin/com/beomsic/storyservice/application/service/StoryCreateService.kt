package com.beomsic.storyservice.application.service

import com.beomsic.storyservice.application.port.`in`.command.PlaceCreateCommand
import com.beomsic.storyservice.application.port.`in`.command.StoryCreateCommand
import com.beomsic.storyservice.application.port.`in`.usecase.StoryCreateUseCase
import com.beomsic.storyservice.application.port.out.StoryCreatePort
import org.springframework.stereotype.Service

@Service
class StoryCreateService(
    private val storyCreatePort: StoryCreatePort
): StoryCreateUseCase {

    // TODO(위 과정은 하나의 트랜잭션으로 동작해주어야 한다.)
    // TODO(이미지에 대한 처리는 오래걸리기 때문에 kafka를 이용, 이벤트가 발행되었다는 것을 보장해주어야 한다.
    // TODO(만약, 이벤트 발행은 보장되었지만 해당 이미지가 업로드 되었다는 것을 어떻게 보장할 수 있을까?)
    override suspend fun execute(storyCreateCommand: StoryCreateCommand, placeCreateCommands: List<PlaceCreateCommand>?) {
        // TODO(userId를 통해 해당 유저가 정말 존재하는지 확인)

        // TODO(story 정보 저장)
        storyCreatePort.create(storyCreateCommand)

        // TODO(장소에 대한 정보를 저장)

    }
}