package es.unex.cum.mdai.movieTracker.data.services;

import es.unex.cum.mdai.movieTracker.data.repository.ColeccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ColeccionServiceImpl implements ColeccionService {

    private final ColeccionRepository coleccionRepository;

    @Autowired
    public ColeccionServiceImpl(ColeccionRepository coleccionRepository) {
        this.coleccionRepository = coleccionRepository;
    }



}
