package it.uniroma3.siw.siwcinefestival.service;

import java.awt.image.BufferedImage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import java.util.NoSuchElementException;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.siwcinefestival.model.Actor;
import it.uniroma3.siw.siwcinefestival.model.Festival;
import it.uniroma3.siw.siwcinefestival.model.Film;

import it.uniroma3.siw.siwcinefestival.repository.ActorRepository;
import it.uniroma3.siw.siwcinefestival.repository.FestivalRepository;
import it.uniroma3.siw.siwcinefestival.repository.FilmRepository;


@Service
public class ImageService {

    private static final Logger logger =
            LoggerFactory.getLogger(ImageService.class);


    /*
     * Dimensione massima: 3 MB.
     */

    private static final long MAX_SIZE =
            3L * 1024 * 1024;


    /*
     * Cartella in cui salviamo le immagini.
     */

    private static final Path UPLOAD_DIRECTORY =
            Path.of("uploads", "images")
                    .toAbsolutePath()
                    .normalize();


    private final FilmRepository filmRepository;

    private final ActorRepository actorRepository;

    private final FestivalRepository festivalRepository;


    public ImageService(
            FilmRepository filmRepository,
            ActorRepository actorRepository,
            FestivalRepository festivalRepository) {

        this.filmRepository = filmRepository;
        this.actorRepository = actorRepository;
        this.festivalRepository = festivalRepository;
    }


    /*
     * =====================================================
     * UPLOAD LOCANDINA FILM
     * =====================================================
     */

    @Transactional
    public void uploadFilmImage(
            Long filmId,
            MultipartFile file) {

        Film film = filmRepository
                .findById(filmId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Film non trovato."
                        )
                );


        String oldFileName = film.getImageFileName();

        String newFileName = saveImage(file);


        registerFileCleanup(
                newFileName,
                oldFileName
        );


        film.setImageFileName(newFileName);

        filmRepository.save(film);
    }


    /*
     * =====================================================
     * UPLOAD FOTO ATTORE
     * =====================================================
     */

    @Transactional
    public void uploadActorImage(
            Long actorId,
            MultipartFile file) {

        Actor actor = actorRepository
                .findById(actorId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Attore non trovato."
                        )
                );


        String oldFileName = actor.getImageFileName();

        String newFileName = saveImage(file);


        registerFileCleanup(
                newFileName,
                oldFileName
        );


        actor.setImageFileName(newFileName);

        actorRepository.save(actor);
    }


    /*
     * =====================================================
     * UPLOAD COPERTINA FESTIVAL
     * =====================================================
     */

    @Transactional
    public void uploadFestivalImage(
            Long festivalId,
            MultipartFile file) {

        Festival festival = festivalRepository
                .findById(festivalId)
                .orElseThrow(() ->
                        new NoSuchElementException(
                                "Festival non trovato."
                        )
                );


        String oldFileName =
                festival.getImageFileName();

        String newFileName = saveImage(file);


        registerFileCleanup(
                newFileName,
                oldFileName
        );


        festival.setImageFileName(newFileName);

        festivalRepository.save(festival);
    }


    /*
     * =====================================================
     * SALVATAGGIO DEL FILE
     * =====================================================
     */

    private String saveImage(MultipartFile file) {

        if (file == null || file.isEmpty()) {

            throw new IllegalArgumentException(
                    "Seleziona un'immagine."
            );
        }


        if (file.getSize() > MAX_SIZE) {

            throw new IllegalArgumentException(
                    "L'immagine non può superare 3 MB."
            );
        }


        try {

            byte[] bytes = file.getBytes();

            String extension;


            /*
             * Controlliamo il formato
             * attraverso la firma del file.
             */

            if (isJpeg(bytes)) {

                extension = "jpg";

            } else if (isPng(bytes)) {

                extension = "png";

            } else {

                throw new IllegalArgumentException(
                        "Sono consentite soltanto immagini JPG e PNG."
                );
            }


            /*
             * Controlliamo che il contenuto
             * sia effettivamente un'immagine.
             */

            BufferedImage image = ImageIO.read(
                    new ByteArrayInputStream(bytes)
            );


            if (image == null) {

                throw new IllegalArgumentException(
                        "Il file selezionato non è un'immagine valida."
                );
            }


            /*
             * Limitiamo anche la risoluzione.
             */

            if (image.getWidth() > 5000
                    || image.getHeight() > 5000) {

                throw new IllegalArgumentException(
                        "L'immagine ha dimensioni troppo grandi."
                );
            }


            /*
             * Creiamo la cartella se necessario.
             */

            Files.createDirectories(
                    UPLOAD_DIRECTORY
            );


            /*
             * Generiamo un nome unico.
             */

            String fileName =
                    UUID.randomUUID() + "." + extension;


            Path destination =
                    UPLOAD_DIRECTORY.resolve(fileName);


            /*
             * Salviamo il file.
             */

            Files.write(
                    destination,
                    bytes,
                    StandardOpenOption.CREATE_NEW
            );


            return fileName;

        } catch (IOException ex) {

            throw new UncheckedIOException(
                    "Errore durante il salvataggio dell'immagine.",
                    ex
            );
        }
    }


    /*
     * =====================================================
     * GESTIONE FILE IN CASO DI ROLLBACK
     * =====================================================
     *
     * Il filesystem non partecipa alle
     * transazioni del database.
     *
     * Per questo registriamo una pulizia:
     *
     * - commit: eliminiamo la vecchia immagine
     * - rollback: eliminiamo la nuova immagine
     */

    private void registerFileCleanup(
            String newFileName,
            String oldFileName) {

        TransactionSynchronizationManager
                .registerSynchronization(
                    new TransactionSynchronization() {

                        @Override
                        public void afterCompletion(
                                int status) {

                            if (status == STATUS_COMMITTED) {

                                if (oldFileName != null
                                        && !oldFileName.equals(
                                                newFileName
                                        )) {

                                    deleteImageQuietly(
                                            oldFileName
                                    );
                                }

                            } else {

                                deleteImageQuietly(
                                        newFileName
                                );
                            }
                        }
                    }
                );
    }


    /*
     * Elimina un file senza interrompere
     * l'operazione già completata.
     */

    private void deleteImageQuietly(
            String fileName) {

        try {

            Files.deleteIfExists(
                    getImagePath(fileName)
            );

        } catch (Exception ex) {

            logger.warn(
                    "Impossibile eliminare l'immagine {}",
                    fileName,
                    ex
            );
        }
    }


    /*
     * =====================================================
     * VERIFICA JPEG
     * =====================================================
     */

    private boolean isJpeg(byte[] bytes) {

        return bytes.length >= 3

                && (bytes[0] & 0xFF) == 0xFF

                && (bytes[1] & 0xFF) == 0xD8

                && (bytes[2] & 0xFF) == 0xFF;
    }


    /*
     * =====================================================
     * VERIFICA PNG
     * =====================================================
     */

    private boolean isPng(byte[] bytes) {

        byte[] signature = {
                (byte) 137,
                80,
                78,
                71,
                13,
                10,
                26,
                10
        };


        if (bytes.length < signature.length) {
            return false;
        }


        for (int i = 0; i < signature.length; i++) {

            if (bytes[i] != signature[i]) {
                return false;
            }
        }


        return true;
    }


    /*
     * =====================================================
     * RECUPERO PERCORSO
     * =====================================================
     */

    public Path getImagePath(String fileName) {

        /*
         * Accettiamo soltanto nomi
         * nel formato UUID.jpg / UUID.png.
         */

        if (fileName == null
                || !fileName.matches(
                    "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}\\.(jpg|png)"
                )) {

            throw new IllegalArgumentException(
                    "Nome immagine non valido."
            );
        }


        Path result = UPLOAD_DIRECTORY
                .resolve(fileName)
                .normalize();


        if (!result.startsWith(UPLOAD_DIRECTORY)) {

            throw new IllegalArgumentException(
                    "Percorso immagine non valido."
            );
        }


        return result;
    }
}