package lk.ijse.dep9.service;

import lk.ijse.dep9.service.custom.impl.BookServiceImpl;
import lk.ijse.dep9.service.custom.impl.IssueServiceImpl;
import lk.ijse.dep9.service.custom.impl.MemberServiceImpl;
import lk.ijse.dep9.service.custom.impl.ReturnServiceImpl;

import java.sql.Connection;

public class ServiceFactory {
    private static ServiceFactory serviceFactory;

    private ServiceFactory(){}

    public static ServiceFactory getInstance(){
        return (serviceFactory == null) ? (serviceFactory = new ServiceFactory()): serviceFactory;
    }

    public <T extends SuperService> T getService(Connection connection, ServiceTypes serviceType){
        switch (serviceType){
            case BOOK:
                return (T) new BookServiceImpl(connection);
            case ISSUE:
                return (T) new IssueServiceImpl(connection);
            case MEMBER:
                return (T) new MemberServiceImpl(connection);
            case RETURN:
                return (T) new ReturnServiceImpl(connection);
            default:
                return null;
        }
    }
}
