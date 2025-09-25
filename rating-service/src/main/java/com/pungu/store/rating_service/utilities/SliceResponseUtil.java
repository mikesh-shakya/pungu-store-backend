package com.pungu.store.rating_service.utilities;

import com.pungu.store.rating_service.dtos.RatingResponse;
import com.pungu.store.rating_service.dtos.SliceResponse;
import com.pungu.store.rating_service.entities.Rating;
import org.springframework.data.domain.Slice;

import java.util.List;

public class SliceResponseUtil {
    public static SliceResponse<RatingResponse> mapToSLiceResponse(List<RatingResponse> content, Slice<Rating> authorList){
        return new SliceResponse<>(
                content,
                authorList.getNumber(),
                authorList.getNumberOfElements(),
                authorList.getSize(),
                authorList.hasNext()
        );
    }
}
