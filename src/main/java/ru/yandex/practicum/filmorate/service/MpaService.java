package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.film.MpaDbStorage;
import ru.yandex.practicum.filmorate.dto.filmdto.MpaDto;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;


@Service
public class MpaService {

    private final MpaDbStorage mpaDbStorage;

    public MpaService(MpaDbStorage mpaDbStorage) {
        this.mpaDbStorage = mpaDbStorage;
    }

    public List<MpaDto> getMpa(){
        return mpaDbStorage.selectMpa().stream()
                .map(MpaMapper::mapToMpaDto)
                .toList();
    }

    public MpaDto getMpaById(Long id){
        Mpa mpa =  mpaDbStorage.selectMpaById(id).orElseThrow(()->
                new NotFoundException("Mpa с id = " + id + " не найден"));
        return MpaMapper.mapToMpaDto(mpa);
    }
}
