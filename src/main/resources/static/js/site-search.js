/* =====================================================
   SIWCINEFESTIVAL — LIVE SEARCH
   ===================================================== */

document.addEventListener("DOMContentLoaded", () => {

    /*
     * Recuperiamo tutte le barre di ricerca.
     *
     * Ogni barra possiede un attributo:
     *
     * data-search-input="nome-gruppo"
     */

    const searchInputs = document.querySelectorAll(
        "[data-search-input]"
    );


    /*
     * Normalizza una stringa.
     *
     * Esempio:
     *
     * "Venezia" -> "venezia"
     *
     * Ignoriamo anche gli accenti.
     */

    function normalizeText(value) {

        return String(value ?? "")

            .normalize("NFD")

            .replace(/[\u0300-\u036f]/g, "")

            .toLocaleLowerCase("it-IT")

            .trim();
    }


    /*
     * Attiviamo ogni barra di ricerca.
     */

    searchInputs.forEach(input => {

        const group = input.dataset.searchInput;


        /*
         * Recuperiamo gli elementi
         * appartenenti a questo gruppo.
         */

        const items = Array.from(
            document.querySelectorAll(
                "[data-search-item]"
            )
        ).filter(item => {

            return item.dataset.searchItem === group;

        });


        /*
         * Eventuale messaggio:
         * Nessun risultato trovato.
         */

        const emptyMessage = Array.from(
            document.querySelectorAll(
                "[data-search-empty]"
            )
        ).find(element => {

            return element.dataset.searchEmpty === group;

        });


        /*
         * Eventuale contatore risultati.
         */

        const counter = Array.from(
            document.querySelectorAll(
                "[data-search-count]"
            )
        ).find(element => {

            return element.dataset.searchCount === group;

        });


        /*
         * Funzione che esegue la ricerca.
         */

        function performSearch() {

            const query = normalizeText(input.value);

            /*
             * Se l'utente scrive più parole,
             * devono essere tutte presenti.
             */

            const words = query
                .split(/\s+/)
                .filter(Boolean);


            let visibleCount = 0;


            items.forEach(item => {

                /*
                 * Per impostazione predefinita
                 * cerchiamo nel testo dell'elemento.
                 *
                 * Se è presente data-search-text,
                 * utilizziamo quel valore.
                 */

                const searchableText = normalizeText(

                    item.dataset.searchText
                    ?? item.textContent

                );


                const matches = words.every(word => {

                    return searchableText.includes(word);

                });


                item.hidden = !matches;


                if (matches) {
                    visibleCount++;
                }

            });


            /*
             * Aggiorniamo il contatore.
             */

            if (counter) {

                counter.textContent =
                    `${visibleCount} risultati`;

            }


            /*
             * Mostriamo il messaggio
             * soltanto quando una ricerca
             * non trova risultati.
             */

            if (emptyMessage) {

                emptyMessage.hidden =

                    query.length === 0

                    ||

                    visibleCount > 0

                    ||

                    items.length === 0;
            }

        }


        /*
         * Ricerca durante la digitazione.
         */

        input.addEventListener(
            "input",
            performSearch
        );


        /*
         * ESC cancella la ricerca.
         */

        input.addEventListener("keydown", event => {

            if (event.key === "Escape") {

                input.value = "";

                performSearch();
            }

        });


        /*
         * Stato iniziale.
         */

        performSearch();

    });

});