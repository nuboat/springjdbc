package thjug.springboot.service;

import java.util.List;
import thjug.springboot.entity.Customer;

public interface CustomerService {

    public List<Customer> queryByFirstname(final String firstName);
}
