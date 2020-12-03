import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.test.context.ContextConfiguration;
import org.xynok.MainApp;
import org.xynok.lab.dao.Cus;
import org.xynok.lab.service.DbService;

@SpringBootTest
@ContextConfiguration(classes = MainApp.class)
public class DataSourceTest {
    @Autowired
    JdbcTemplate JdbcTemplateDefault;
    @Autowired
    @Qualifier("cus1")
    JdbcTemplate JdbcTemplateCus1;
    @Autowired
    @Qualifier("cus2")
    JdbcTemplate JdbcTemplateCus2;

    @Test
    public void jdbcTemplate() {
        String sql = "select name,value from cus";
        
        JdbcTemplateDefault.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                Cus cus=new Cus();
                cus.setName(rs.getString("name"));
                cus.setValue(rs.getString("value"));
                System.out.println(String.format("Default jdbcTemplate result:%s",cus));
            }
        });
        JdbcTemplateCus1.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                Cus cus=new Cus();
                cus.setName(rs.getString("name"));
                cus.setValue(rs.getString("value"));
                System.out.println(String.format("Cus1 jdbcTemplate result:%s",cus));
            }
        });
        JdbcTemplateCus2.query(sql, new RowCallbackHandler() {
            @Override
            public void processRow(ResultSet rs) throws SQLException {
                Cus cus=new Cus();
                cus.setName(rs.getString("name"));
                cus.setValue(rs.getString("value"));
                System.out.println(String.format("Cus2 jdbcTemplate result:%s.",cus));
            }
        });
    }

    @Autowired
    DbService dbService;

    @Test
    void multiDs(){
        List<Cus> list=dbService.allCus();
        list.forEach(item->{
            System.out.println(item);
        });
        list=dbService.allCus1();
        list.forEach(item->{
            System.out.println(item);
        });
        list=dbService.allCus2();
        list.forEach(item->{
            System.out.println(item);
        });
    }
}