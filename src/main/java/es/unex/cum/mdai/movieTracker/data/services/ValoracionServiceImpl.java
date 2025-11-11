package es.unex.cum.mdai.movieTracker.data.services;

import es.unex.cum.mdai.movieTracker.data.repository.ValoracionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ValoracionServiceImpl implements  ValoracionService {

    private final ValoracionRepository valoracionRepository;

    @Autowired
    public ValoracionServiceImpl(ValoracionRepository valoracionRepository) {
        this.valoracionRepository = valoracionRepository;
    }


}
