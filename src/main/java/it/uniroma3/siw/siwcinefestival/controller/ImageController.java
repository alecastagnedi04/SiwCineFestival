package it.uniroma3.siw.siwcinefestival.controller;

import java.io.UncheckedIOException;

import java.nio.file.Path;

import java.util.NoSuchElementException;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.multipart.MultipartFile;

import org.springframework.web.server.ResponseStatusException;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import it.uniroma3.siw.siwcinefestival.service.ImageService;


@Controller
public class ImageController {

    private final ImageService imageService;


    public ImageController(
            ImageService imageService) {

        this.imageService = imageService;
    }


    /*
     * =====================================================
     * UPLOAD IMMAGINI
     * =====================================================
     *
     * Soltanto ADMIN.
     */

    @PostMapping("/admin/media/{type}/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String uploadImage(

            @PathVariable String type,

            @PathVariable Long id,

            @RequestParam("image")
            MultipartFile image,

            RedirectAttributes redirectAttributes) {


        /*
         * Determiniamo la pagina
         * a cui tornare.
         */

        String redirectUrl = switch (type) {

            case "film" ->
                    "/admin/films/" + id + "/edit";

            case "actor" ->
                    "/admin/actors/" + id + "/edit";

            case "festival" ->
                    "/admin/festivals/" + id + "/edit";

            default -> throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Tipo di immagine non valido."
            );
        };


        try {

            /*
             * Deleghiamo al Service.
             */

            switch (type) {

                case "film" ->
                        imageService.uploadFilmImage(
                                id,
                                image
                        );

                case "actor" ->
                        imageService.uploadActorImage(
                                id,
                                image
                        );

                case "festival" ->
                        imageService.uploadFestivalImage(
                                id,
                                image
                        );

                default -> throw new IllegalArgumentException(
                        "Tipo non valido."
                );
            }


            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Immagine caricata con successo!"
            );

        } catch (
                IllegalArgumentException
                | NoSuchElementException
                | UncheckedIOException ex
        ) {

            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    ex.getMessage()
            );
        }


        return "redirect:" + redirectUrl;
    }


    /*
     * =====================================================
     * VISUALIZZAZIONE PUBBLICA
     * =====================================================
     */

    @GetMapping("/media/{fileName:.+}")
    public ResponseEntity<Resource> getImage(

            @PathVariable String fileName) {


        try {

            Path path =
                    imageService.getImagePath(fileName);


            Resource resource =
                    new FileSystemResource(path);


            if (!resource.exists()
                    || !resource.isReadable()) {

                return ResponseEntity.notFound().build();
            }


            MediaType mediaType =
                    fileName.endsWith(".png")

                            ? MediaType.IMAGE_PNG

                            : MediaType.IMAGE_JPEG;


            return ResponseEntity.ok()

                    .contentType(mediaType)

                    .body(resource);


        } catch (IllegalArgumentException ex) {

            return ResponseEntity.notFound().build();
        }
    }
}