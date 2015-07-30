package thjug.springboot.service;

import java.util.List;
import thjug.springboot.entity.Customer;

public interface CustomerService {

    public List<Customer> queryByFirstname(final String firstName);

    public Customer read(final Long id);

    public Customer create(final Customer customer);

    public void update(final Customer customer);

    public void delete(final Long id);

    public List<Customer> bulkCreate(final List<Customer> customers);
}
