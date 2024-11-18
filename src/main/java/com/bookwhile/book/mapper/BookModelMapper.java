package com.bookwhile.book.mapper;

import com.bookwhile.book.dto.BookResponseDto;
import com.bookwhile.book.dto.CreateBookRequestDto;
import com.bookwhile.book.dto.UpdateBookRequestDto;
import com.bookwhile.book.model.BookResponse;
import com.bookwhile.book.model.CreateBookRequest;
import com.bookwhile.book.model.UpdateBookRequest;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookModelMapper {

    CreateBookRequestDto toBookRequestDto(CreateBookRequest createBookRequest);

    BookResponse toBookResponse(BookResponseDto bookResponseDto);

    List<BookResponse> toBookResponseList(List<BookResponseDto> bookResponseDtoList);

    UpdateBookRequestDto toUpdateBookRequestDto(UpdateBookRequest updateBookRequest);
}
