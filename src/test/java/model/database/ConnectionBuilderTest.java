package model.database;

import static org.junit.Assert.*;

import java.sql.Connection;

import model.database.ConnectionBuilder;

import org.junit.Test;

public class ConnectionBuilderTest {

	@Test
	public void testGetConnection() {
		Connection connection = ConnectionBuilder.getConnection();
		assertNotNull(connection);
	}

}
