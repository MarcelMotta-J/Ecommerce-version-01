package com.mrcl.store1.dao;

import com.mrcl.store1.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;


@RepositoryRestResource(collectionResourceRel = "states", path = "states")
public interface StateRepository extends JpaRepository<State, Integer> {
    // http://localhost:8080/api/states

    // http://localhost:8080/api/states/search/findByCountryCode?code=BR
    List<State> findByCountryCode(@Param("code") String code);

}
