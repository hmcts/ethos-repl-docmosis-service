package uk.gov.hmcts.ethos.replacement.docmosis.service.jpaservice;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.ecm.common.model.bulk.types.DynamicValueType;
import uk.gov.hmcts.ethos.replacement.docmosis.domain.repository.RoomRepository;
import uk.gov.hmcts.ethos.replacement.docmosis.service.RoomService;

import java.util.List;

@RequiredArgsConstructor
@Service
public class JpaRoomService implements RoomService {

    @Autowired
    private final RoomRepository roomRepository;

    public List<DynamicValueType> getRooms(String venueId){
        return roomRepository.getRoomsByVenueId(venueId);
    }
}
