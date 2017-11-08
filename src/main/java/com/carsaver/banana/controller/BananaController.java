package com.carsaver.banana.controller;

import com.carsaver.banana.domain.Banana;
import com.carsaver.banana.domain.BananaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("/bananas")
@Slf4j
public class BananaController {

    @Autowired
    private BananaRepository bananaRepository;

    @Autowired
    private BananaResourceAssembler bananaResourceAssembler;

    @PostMapping
    public @ResponseBody BananaResource create(@RequestBody Banana banana) {
        banana = bananaRepository.save(banana);
        return bananaResourceAssembler.toResource(banana);
    }

    @GetMapping
    public @ResponseBody List<BananaResource> search(
            @RequestParam(value = "pickedAfter", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime pickedAfter,
            @RequestParam(value = "peeled", required = false)
            Boolean peeled
    ) {
        return StreamSupport.stream(bananaRepository.findAll().spliterator(), false)
                .filter(b -> {
                    if (Objects.nonNull(pickedAfter) && b.getPickedAt().isBefore(pickedAfter)) {
                        return false;
                    } else if (Objects.nonNull(peeled) && !b.getPeeled().equals(peeled)) {
                        return false;
                    } else {
                        return true;
                    }
                })
                .map(bananaResourceAssembler::toResource)
                .collect(Collectors.toList());
    }

    @RequestMapping(path = "/{id}", method = {RequestMethod.GET, RequestMethod.HEAD})
    public @ResponseBody BananaResource retrieve(@PathVariable long id) {
        return bananaResourceAssembler.toResource(bananaRepository.findOne(id));
    }

    @PutMapping("/{id}")
    public @ResponseBody BananaResource update(@PathVariable long id, @RequestBody Banana banana) {
        banana.setId(id);
        return bananaResourceAssembler.toResource(bananaRepository.save(banana));
    }

    @PatchMapping("/{id}")
    public @ResponseBody BananaResource patch(@PathVariable long id, @RequestBody Banana banana) {
        Banana existing = bananaRepository.findOne(id);
        if (Objects.isNull(banana)) {
            return null;
        }

        if (Objects.nonNull(banana.getPickedAt())) {
            existing.setPickedAt(banana.getPickedAt());
        }

        if (Objects.nonNull(banana.getPeeled())) {
            existing.setPeeled(banana.getPeeled());
        }

        return bananaResourceAssembler.toResource(bananaRepository.save(existing));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable long id) {
        Banana banana = bananaRepository.findOne(id);
        if (Objects.isNull(banana)) {
            return ResponseEntity.notFound().build();
        }

        bananaRepository.delete(banana);
        return ResponseEntity.ok().build();
    }

    @ExceptionHandler(NullPointerException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public void handleNullPointerException(NullPointerException ex, HttpServletResponse response) {
        log.warn("handling NullPointerException", ex);
    }
}
