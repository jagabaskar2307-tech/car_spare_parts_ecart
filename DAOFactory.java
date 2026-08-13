package com.jagadeesh.jagadeeshcart.dao;

import com.jagadeesh.jagadeeshcart.dao.impl.UserDAOImpl;
import com.jagadeesh.jagadeeshcart.listener.DataSourceListener;

/** Factory pattern: hands out DAO instances backed by the shared connection pool. */
public final class DAOFactory {

    private DAOFactory() {
    }

    public static UserDAO userDAO() {
        return new UserDAOImpl(DataSourceListener.getDataSource());
    }
}
