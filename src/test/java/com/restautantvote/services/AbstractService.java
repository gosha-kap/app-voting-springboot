package com.restautantvote.services;

import com.restautantvote.AbstractControllerTest;
import com.restautantvote.repository.UserRepository;
import com.restautantvote.utils.JpaUtil;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;

public class AbstractService extends AbstractControllerTest {

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected JpaUtil jpaUtil;

    @Before
    public void setUp()  {
              jpaUtil.clear2ndLevelHibernateCache();
    }

}
