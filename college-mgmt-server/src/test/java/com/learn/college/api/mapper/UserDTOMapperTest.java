package com.learn.college.api.mapper;

import com.learn.college.common.dto.UserDTO;
import io.vertx.sqlclient.Row;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;

import static com.learn.college.common.config.Constants.*;
import static com.learn.college.common.config.TestConstants.*;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.when;

@RunWith(PowerMockRunner.class)
public class UserDTOMapperTest {

  @Mock private Row rs;

  @Before
  public void before() {
    when(rs.getString(DB_USER_ID)).thenReturn(TEST_USER_ID);
    when(rs.getString(DB_FIRST_NAME)).thenReturn("");
    when(rs.getString(DB_LAST_NAME)).thenReturn("");
    when(rs.getString(DB_EMAIL)).thenReturn(TEST_EMAIL);
    when(rs.getString(DB_PASSWORD)).thenReturn(TEST_PASS);
  }

  @Test
  public void testUserMapper_P() {
    UserDTO userDTO = UserDTOMapper.getInstance().mapRow(rs);

    assertEquals(TEST_USER_ID, userDTO.getUserId());
    assertEquals("", userDTO.getFirstName());
    assertEquals("", userDTO.getLastName());
    assertEquals(TEST_EMAIL, userDTO.getEmail());
    assertEquals(TEST_PASS, userDTO.getPassword());
  }
}
