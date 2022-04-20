package com.restautantvote.repository;

import com.restautantvote.model.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MenuRepository extends JpaRepository<Menu,Integer>
{


}
