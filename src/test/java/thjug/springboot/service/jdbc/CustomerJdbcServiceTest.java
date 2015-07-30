package thjug.springboot.service.jdbc;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import thjug.springboot.Application;
import thjug.springboot.entity.Customer;
import thjug.springboot.service.CustomerService;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class)
public class CustomerJdbcServiceTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private CustomerService customerService;

    @Before
    public void createDB() {
        log.info("Creating tables");

        jdbcTemplate.execute("DROP TABLE customers IF EXISTS");
        jdbcTemplate.execute("CREATE TABLE customers("
            + "id SERIAL, first_name VARCHAR(16), last_name VARCHAR(16))");

        // Split up the array of whole names into an array of first/last names
        final List<Object[]> splitUpNames =
            Arrays.asList("John Woo", "Jeff Dean", "Josh Bloch", "Josh Long")
            .stream()
            .map(name -> name.split(" "))
            .collect(Collectors.toList());

        // Use a Java 8 stream to print out each tuple of the list
        splitUpNames.forEach(name ->
                log.info(String.format("Inserting customer record for %s %s",
                        name[0],
                        name[1])));

        // Uses JdbcTemplate's batchUpdate operation to bulk load data
        jdbcTemplate.batchUpdate(
                "INSERT INTO customers(first_name, last_name) VALUES (?, ?)",
                splitUpNames);
    }

    @Test
    public void testQueryCustomer() {
        log.info("Querying for customer records where first_name = 'Josh':");
        jdbcTemplate.query(
                "SELECT id, first_name, last_name FROM customers WHERE first_name = ?"
                , new Object[]{"Josh"}
                , (rs, rowNum) -> new Customer(rs.getLong("id"), rs.getString("first_name"), rs.getString("last_name"))
        ).forEach(customer -> log.info(customer.toString()));
    }

    @Test
    public void testFindFirstnameJosh() {
        final String name = "Josh";
        final List<Customer> customers = customerService.queryByFirstname(name);

        Assert.assertThat(customers.get(0).getFirstName(), Matchers.equalTo(name));
    }

    @Test
    public void testRead() {
        final long id = 1L;
        final Customer result = customerService.read(id);

        log.info("Customer: {}", result.toString());

        Assert.assertTrue(result.getId() == id);
    }

    @Test
    public void testCreate() {
        final Customer customer = new Customer(null, "Mickey", "Mouse");

        final Customer result = customerService.create(customer);

        log.info("Customer: {}", result.toString());

        Assert.assertTrue(result.getId() != null);
    }

    @Test
    public void testUpdate() {
        final Customer customer = new Customer(1L, "Mickey", "Mouse II");

        customerService.update(customer);
    }

    @Test
    public void testDelete() {
        customerService.delete(1L);
    }

    @Test(expected=RuntimeException.class)
    public void testbulkCreate() {
        final List<Customer> customers = Arrays.asList(
                new Customer(null, "Bulk", "Create"),
                new Customer(null, "BulkBulkBulkBulkBulkBulk", "Create"));

        try {
            customerService.bulkCreate(customers);
        } catch (final Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        } finally {
            final List<Customer> c = customerService.queryByFirstname("Bulk");
            log.info("List<Customer>: {} ", c.toString());
        }
    }

}
