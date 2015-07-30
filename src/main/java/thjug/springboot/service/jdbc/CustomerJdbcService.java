package thjug.springboot.service.jdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import thjug.springboot.entity.Customer;
import thjug.springboot.service.CustomerService;

@Component
public class CustomerJdbcService implements CustomerService {

    private static final String QUERY_BY_ID =
        "SELECT id, first_name, last_name FROM customers WHERE id = ?";

    private static final String QUERY_BY_FIRSTNAMENAME =
        "SELECT id, first_name, last_name FROM customers WHERE first_name = ?";

    private static final String INSERT =
        "INSERT INTO customers(first_name, last_name) VALUES (?, ?)";

    private static final String UPDATE =
        "UPDATE customers SET first_name = ?, last_name = ? WHERE id = ?";

    private static final String DELETE =
        "DELETE customers WHERE id = ?";

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Customer> queryByFirstname(final String firstName) {
        return jdbcTemplate.query(
                QUERY_BY_FIRSTNAMENAME,
                new Object[]{firstName},
                new CustomerRowMapper());
    }

    @Override
    public Customer read(final Long id) {
        return jdbcTemplate.query(
                QUERY_BY_ID, new Object[]{id}, new CustomerExtractor());
    }

    @Override
    public Customer create(final Customer customer) {
        final KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update((Connection connection) -> {
            final PreparedStatement ps =
                    connection.prepareStatement(INSERT, new String[] {"id"});
            ps.setString(1, customer.getFirstName());
            ps.setString(2, customer.getLastName());
            return ps;
        }, keyHolder);

        customer.setId(keyHolder.getKey().longValue());
        return customer;
    }

    @Override
    public void update(final Customer customer) {
        final int count = jdbcTemplate.update(UPDATE,
                customer.getFirstName(),
                customer.getLastName(),
                customer.getId());
        assert count != 0;
    }

    @Override
    public void delete(final Long id) {
        final int count = jdbcTemplate.update(DELETE, id);
        assert count != 0;
    }

    /**
     * (rs, rowNum) -> new Customer(rs.getLong("id"),
     *                              rs.getString("first_name"),
     *                              rs.getString("last_name"))
     */
    class CustomerRowMapper implements RowMapper<Customer> {

        @Override
        public Customer mapRow(final ResultSet rs, final int i)
                throws SQLException {
            return new Customer(rs.getLong("id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"));
        }
    }

    class CustomerExtractor implements ResultSetExtractor<Customer> {

        @Override
        public Customer extractData(final ResultSet rs)
                throws SQLException, DataAccessException {
            assert rs.next();
            return new Customer(rs.getLong("id"),
                    rs.getString("first_name"),
                    rs.getString("last_name"));
        }
    }

}
