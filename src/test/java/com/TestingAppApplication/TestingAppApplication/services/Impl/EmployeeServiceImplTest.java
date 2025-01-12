package com.TestingAppApplication.TestingAppApplication.services.Impl;

import com.TestingAppApplication.TestingAppApplication.TestContainerConfiguration;
import com.TestingAppApplication.TestingAppApplication.dto.EmployeeDto;
import com.TestingAppApplication.TestingAppApplication.entities.Employee;
import com.TestingAppApplication.TestingAppApplication.exceptions.ResourceNotFoundException;
import com.TestingAppApplication.TestingAppApplication.repositories.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.StatusResultMatchersExtensionsKt.isEqualTo;

@Import(TestContainerConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private  EmployeeRepository employeeRepository;

    @Spy
    private  ModelMapper modelMapper;

    @InjectMocks
    private EmployeeServiceImpl employeeService;


    private Employee mockEmployee;
    private EmployeeDto mockEmployeeDto;


    @BeforeEach
    void setUp(){
        mockEmployee = Employee.builder()
                .id(1L)
                .email("abhishek@gmail.com")
                .name("Abhishek")
                .salary(200L)
                .build();

        mockEmployeeDto = modelMapper.map(mockEmployee,EmployeeDto.class);

    }

    @Test
    void testGetEmployeeById_WhenEmployeeIdIsPresent_ThenReturnEmployeeDto(){
//        employeeService.getEmployeeById(1L);

//        assign
//        Stubbing
        Long id = mockEmployee.getId();
        when(employeeRepository.findById(id)).thenReturn(Optional.of(mockEmployee));


//        act

        EmployeeDto employeeDto = employeeService.getEmployeeById(id);


//        assert

        assertThat(employeeDto.getId()).isEqualTo(id);
        assertThat(employeeDto.getEmail()).isEqualTo(mockEmployee.getEmail());

        verify(employeeRepository,only()).findById(id);
    }


    @Test
    void testGetEmployeeById_whenEmployeeIsNotPresent_thenThrowException(){
//        arrange

        when(employeeRepository.findById(anyLong())).thenReturn(Optional.empty());

//        act and assert

        assertThatThrownBy(()->employeeService.getEmployeeById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Employee not found with id: 1");

          verify(employeeRepository).findById(1L);
    }


    @Test
    void testCreateNewEmployee_WhenValidEmployee_ThenCreateNewEmployee(){
//        assign
        when(employeeRepository.findByEmail(anyString())).thenReturn(List.of());
        when(employeeRepository.save(any(Employee.class))).thenReturn(mockEmployee);
//        act

         EmployeeDto employeeDto = employeeService.createNewEmployee(mockEmployeeDto);

//        assert

        assertThat(employeeDto).isNotNull();
        assertThat(employeeDto.getEmail()).isEqualTo(mockEmployeeDto.getEmail());


        ArgumentCaptor<Employee> employeeArgumentCaptor = ArgumentCaptor.forClass(Employee.class);
        verify(employeeRepository).save(employeeArgumentCaptor.capture());

        Employee capturedEmployee = employeeArgumentCaptor.getValue();
        assertThat(capturedEmployee.getEmail()).isEqualTo(mockEmployee.getEmail());

    }

    @Test
    void testCreateNewEmployee_whenAttemptingToCreateEmployeeWithExistingEmail_thenThrowException(){
//        arrange
        when(employeeRepository.findByEmail(mockEmployeeDto.getEmail())).thenReturn(List.of(mockEmployee));
//                act and arrange

        assertThatThrownBy(()->employeeService.createNewEmployee(mockEmployeeDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Employee already exists with email: "+mockEmployee.getEmail());

                verify(employeeRepository).findByEmail(mockEmployeeDto.getEmail());
                verify(employeeRepository,never()).save(any());
    }

    @Test
    void testUpdateEmployee_whenEmployeeDoesNotExists_thenThrowException(){
//        arrange
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

//        act and assert

      assertThatThrownBy(()-> employeeService.updateEmployee(1L,mockEmployeeDto))
              .isInstanceOf(ResourceNotFoundException.class)
              .hasMessage("Employee not found with id: 1");

           verify(employeeRepository).findById(1L);
           verify(employeeRepository,never()).save(any());
    }

    @Test
    void testUpdateEmail_whenNotAllowedToUpdateEmail_thenThrowException(){
//        arrange
        when(employeeRepository.findById(mockEmployeeDto.getId())).thenReturn(Optional.of(mockEmployee));
        mockEmployeeDto.setName("Random");
        mockEmployeeDto.setEmail(("random@gmail.com"));


//        act and assert
        assertThatThrownBy(()-> employeeService.updateEmployee(mockEmployeeDto.getId(),mockEmployeeDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("The email of the employee cannot be updated");

        verify(employeeRepository).findById(mockEmployeeDto.getId());
        verify(employeeRepository,never()).save(any());


    }

    @Test
    void testUpdateEmployee_whenValidEmployee_thenUpdateEmployee(){
//        arrange

        when(employeeRepository.findById(mockEmployeeDto.getId())).thenReturn(Optional.of(mockEmployee));
        mockEmployeeDto.setName("Random Name");
        mockEmployeeDto.setSalary(199L);

        Employee newEmployee = modelMapper.map(mockEmployeeDto, Employee.class);
        when(employeeRepository.save(any(Employee.class))).thenReturn(newEmployee);


//        act
        EmployeeDto updatedEmployeeDto = employeeService.updateEmployee(mockEmployeeDto.getId(),mockEmployeeDto);

        assertThat(updatedEmployeeDto).isEqualTo(mockEmployeeDto);

        verify(employeeRepository).findById(1L);
        verify(employeeRepository).save(any());



    }

    @Test
    void testDeleteEmployee_whenEmployeeDoesNotExists_thenThrowException(){
        when(employeeRepository.existsById(1L)).thenReturn(false);

//        act
        assertThatThrownBy(()->employeeService.deleteEmployee(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Employee not found with id: "+1L);
        verify(employeeRepository,never()).deleteById(anyLong());
    }

    @Test
    void testDeleteEmployee_whenEmployeeIsValid_thenDeleteEmployee(){
        when(employeeRepository.existsById(1L)).thenReturn(true);
        assertThatCode(()-> employeeService.deleteEmployee(1L))
                .doesNotThrowAnyException();

        verify(employeeRepository).deleteById(1L);
    }



}