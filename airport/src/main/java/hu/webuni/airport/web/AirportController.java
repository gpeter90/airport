package hu.webuni.airport.web;

import hu.webuni.airport.dto.AirportDto;
import hu.webuni.airport.mapper.AirportMapper;
import hu.webuni.airport.model.Airport;
import hu.webuni.airport.service.AirportService;
import hu.webuni.airport.service.LogEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/api/airports")
public class AirportController {

    @Autowired
    AirportService airportService;

    @Autowired
    AirportMapper airportMapper;

    @Autowired
    LogEntryService logEntryService;


    @GetMapping
    public List<AirportDto> getAll() {
        return airportMapper.airportsToDtos(airportService.findAll());
    }

    @GetMapping("/{id}")
    public AirportDto getById(@PathVariable long id) {
        Airport airport = airportService.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return airportMapper.airportsToDto(airport);

    }

    @PostMapping
    public AirportDto createAirport(@RequestBody @Valid AirportDto airportDto) {
        Airport airport = airportService.save(airportMapper.dtoToAirport(airportDto));
        return airportMapper.airportsToDto(airport);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<AirportDto> modifyAirport(@PathVariable long id, @RequestBody AirportDto airportDto) {
        Airport airport = airportMapper.dtoToAirport(airportDto);
        airport.setId(id);
        try {
            AirportDto savedAirportDto = airportMapper.airportsToDto(airportService.update(airport));
            logEntryService.createLog("Airport modified with id " + id);
            return ResponseEntity.ok(savedAirportDto);
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }


    @DeleteMapping("{id}")
    public void deleteAirport(@PathVariable long id) {
        airportService.delete(id);
    }



}
