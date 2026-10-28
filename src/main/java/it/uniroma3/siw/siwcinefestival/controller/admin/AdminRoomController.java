package it.uniroma3.siw.siwcinefestival.controller.admin;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.uniroma3.siw.siwcinefestival.dto.CinemaRoomForm;
import it.uniroma3.siw.siwcinefestival.model.CinemaRoom;
import it.uniroma3.siw.siwcinefestival.service.CinemaRoomService;


@Controller
@RequestMapping("/admin/rooms")
@PreAuthorize("hasRole('ADMIN')")
public class AdminRoomController {

    private final CinemaRoomService roomService;


    public AdminRoomController(
            CinemaRoomService roomService) {

        this.roomService = roomService;
    }


    /*
     * =====================================================
     * ELENCO SALE
     * =====================================================
     */

    @GetMapping
    public String listRooms(Model model) {

        model.addAttribute(
                "rooms",
                roomService.findAll()
        );

        return "admin/rooms/list";
    }


    /*
     * =====================================================
     * FORM NUOVA SALA
     * =====================================================
     */

    @GetMapping("/new")
    public String newRoomForm(Model model) {

        model.addAttribute(
                "roomForm",
                new CinemaRoomForm()
        );

        prepareForm(model, false, null);

        return "admin/rooms/form";
    }


    /*
     * =====================================================
     * CREAZIONE SALA
     * =====================================================
     */

    @PostMapping("/new")
    public String createRoom(

            @Valid
            @ModelAttribute("roomForm")
            CinemaRoomForm form,

            BindingResult bindingResult,

            Model model,

            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {

            prepareForm(model, false, null);

            return "admin/rooms/form";
        }

        try {

            roomService.createRoom(
                    form.getName(),
                    form.getAddress(),
                    form.getCapacity()
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Sala creata con successo!"
            );

            return "redirect:/admin/rooms";

        } catch (IllegalArgumentException ex) {

            bindingResult.reject(
                    "roomError",
                    ex.getMessage()
            );

            prepareForm(model, false, null);

            return "admin/rooms/form";
        }
    }


    /*
     * =====================================================
     * FORM MODIFICA SALA
     * =====================================================
     */

    @GetMapping("/{id}/edit")
    public String editRoomForm(

            @PathVariable("id") Long id,

            Model model) {

        CinemaRoom room = requireRoom(id);

        CinemaRoomForm form = new CinemaRoomForm();

        form.setName(room.getName());
        form.setAddress(room.getAddress());
        form.setCapacity(room.getCapacity());

        model.addAttribute(
                "roomForm",
                form
        );

        prepareForm(model, true, id);

        return "admin/rooms/form";
    }


    /*
     * =====================================================
     * MODIFICA SALA
     * =====================================================
     */

    @PostMapping("/{id}/edit")
    public String updateRoom(

            @PathVariable("id") Long id,

            @Valid
            @ModelAttribute("roomForm")
            CinemaRoomForm form,

            BindingResult bindingResult,

            Model model,

            RedirectAttributes redirectAttributes) {

        requireRoom(id);

        if (bindingResult.hasErrors()) {

            prepareForm(model, true, id);

            return "admin/rooms/form";
        }

        try {

            roomService.updateRoom(
                    id,
                    form.getName(),
                    form.getAddress(),
                    form.getCapacity()
            );

            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Sala modificata con successo!"
            );

            return "redirect:/admin/rooms";

        } catch (IllegalArgumentException ex) {

            bindingResult.reject(
                    "roomError",
                    ex.getMessage()
            );

            prepareForm(model, true, id);

            return "admin/rooms/form";
        }
    }


    /*
     * =====================================================
     * METODI PRIVATI
     * =====================================================
     */

    private CinemaRoom requireRoom(Long id) {

        return roomService.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Sala non trovata."
                        )
                );
    }


    private void prepareForm(
            Model model,
            boolean isEdit,
            Long roomId) {

        model.addAttribute(
                "isEdit",
                isEdit
        );

        model.addAttribute(
                "roomId",
                roomId
        );
    }
}