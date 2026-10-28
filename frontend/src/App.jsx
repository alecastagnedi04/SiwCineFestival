import { useEffect, useMemo, useState } from "react";

import {
    BarChart,
    Bar,
    CartesianGrid,
    XAxis,
    YAxis,
    Tooltip,
    ResponsiveContainer
} from "recharts";


/* =====================================================
   FUNZIONI DI SUPPORTO
   ===================================================== */

function formatDate(dateString) {

    if (!dateString) {
        return "";
    }

    const date = new Date(
        `${dateString}T12:00:00`
    );

    return new Intl.DateTimeFormat("it-IT", {
        day: "numeric",
        month: "long",
        year: "numeric"
    }).format(date);
}


function formatShortDate(dateString) {

    const date = new Date(
        `${dateString}T12:00:00`
    );

    return new Intl.DateTimeFormat("it-IT", {
        day: "2-digit",
        month: "short"
    }).format(date);
}


function formatTime(timeString) {

    return timeString
        ? timeString.substring(0, 5)
        : "";
}


function getStatusLabel(status) {

    switch (status) {

        case "SCHEDULED":
            return "In programma";

        case "COMPLETED":
            return "Conclusa";

        case "CANCELLED":
            return "Annullata";

        default:
            return status;
    }
}


/* =====================================================
   COMPONENTE 1 — SELETTORE FESTIVAL
   ===================================================== */

function FestivalSelector({
    festivals,
    festivalId,
    onChange
}) {

    return (

        <div className="react-field">

            <label htmlFor="festival-select">

                FESTIVAL

            </label>

            <select
                id="festival-select"
                value={festivalId}
                onChange={(event) =>
                    onChange(event.target.value)
                }
            >

                {festivals.map((festival) => (

                    <option
                        key={festival.id}
                        value={festival.id}
                    >

                        {festival.name} ({festival.year})

                    </option>

                ))}

            </select>

        </div>
    );
}


/* =====================================================
   COMPONENTE 2 — FILTRI
   ===================================================== */

function ProgramFilters({

    screenings,

    search,
    setSearch,

    date,
    setDate,

    roomId,
    setRoomId,

    showCancelled,
    setShowCancelled,

    onReset

}) {

    /* Date disponibili */

    const dates = [

        ...new Set(
            screenings.map(
                screening => screening.date
            )
        )

    ].sort();


    /* Sale disponibili */

    const rooms = Array.from(

        new Map(

            screenings.map(screening => [

                screening.roomId,

                {
                    id: screening.roomId,
                    name: screening.roomName
                }

            ])

        ).values()

    ).sort((a, b) =>
        a.name.localeCompare(b.name)
    );


    return (

        <div className="react-filter-panel">

            <div className="react-filter-heading">

                <div>

                    <span className="react-eyebrow">

                        ✦ FIND YOUR SCREENING

                    </span>

                    <h2>

                        Esplora il programma.

                    </h2>

                </div>

                <button
                    type="button"
                    className="react-reset"
                    onClick={onReset}
                >

                    Azzera filtri ↺

                </button>

            </div>


            <div className="react-filters-grid">


                {/* RICERCA */}

                <div className="react-field">

                    <label htmlFor="film-search">

                        CERCA UN FILM

                    </label>

                    <input
                        id="film-search"
                        type="search"
                        placeholder="Es. Interstellar"
                        value={search}
                        onChange={(event) =>
                            setSearch(event.target.value)
                        }
                    />

                </div>


                {/* DATA */}

                <div className="react-field">

                    <label htmlFor="date-filter">

                        DATA

                    </label>

                    <select
                        id="date-filter"
                        value={date}
                        onChange={(event) =>
                            setDate(event.target.value)
                        }
                    >

                        <option value="">

                            Tutte le date

                        </option>

                        {dates.map(day => (

                            <option
                                key={day}
                                value={day}
                            >

                                {formatDate(day)}

                            </option>

                        ))}

                    </select>

                </div>


                {/* SALA */}

                <div className="react-field">

                    <label htmlFor="room-filter">

                        SALA

                    </label>

                    <select
                        id="room-filter"
                        value={roomId}
                        onChange={(event) =>
                            setRoomId(event.target.value)
                        }
                    >

                        <option value="">

                            Tutte le sale

                        </option>

                        {rooms.map(room => (

                            <option
                                key={room.id}
                                value={room.id}
                            >

                                {room.name}

                            </option>

                        ))}

                    </select>

                </div>

            </div>


            {/* PROIEZIONI ANNULLATE */}

            <label className="react-checkbox">

                <input
                    type="checkbox"
                    checked={showCancelled}
                    onChange={(event) =>
                        setShowCancelled(
                            event.target.checked
                        )
                    }
                />

                Mostra anche le proiezioni annullate

            </label>

        </div>
    );
}


/* =====================================================
   COMPONENTE 3 — TIMELINE PROIEZIONI
   ===================================================== */

function ScreeningTimeline({ screenings }) {

    if (screenings.length === 0) {

        return (

            <div className="react-empty">

                <div className="react-empty-icon">

                    ✦

                </div>

                <h3>

                    Nessuna proiezione trovata.

                </h3>

                <p>

                    Prova a modificare i filtri
                    per visualizzare altri risultati.

                </p>

            </div>
        );
    }


    /* Raggruppamento per giorno */

    const grouped = screenings.reduce(

        (groups, screening) => {

            const day = screening.date;

            if (!groups[day]) {

                groups[day] = [];
            }

            groups[day].push(screening);

            return groups;

        },

        {}
    );


    return (

        <div className="react-timeline">

            {Object.entries(grouped).map(

                ([day, items]) => (

                    <section
                        key={day}
                        className="react-day-section"
                    >


                        {/* GIORNATA */}

                        <div className="react-day-heading">

                            <div>

                                <span className="react-eyebrow">

                                    ✦ OFFICIAL SCREENINGS

                                </span>

                                <h3>

                                    {formatDate(day)}

                                </h3>

                            </div>

                            <span className="react-count-badge">

                                {items.length} proiezioni

                            </span>

                        </div>


                        {/* PROIEZIONI */}

                        <div className="react-screenings-list">

                            {items.map(screening => (

                                <article

                                    key={screening.id}

                                    className={
                                        `react-screening-card ${
                                            screening.status === "CANCELLED"
                                                ? "is-cancelled"
                                                : ""
                                        }`
                                    }
                                >


                                    {/* ORARIO */}

                                    <div className="react-screening-time">

                                        <span>

                                            START TIME

                                        </span>

                                        <strong>

                                            {formatTime(
                                                screening.time
                                            )}

                                        </strong>

                                    </div>


                                    {/* FILM */}

                                    <div className="react-screening-content">

                                        <span className="react-eyebrow">

                                            THE OFFICIAL PROGRAM

                                        </span>


                                        <h4>

                                            {screening.filmTitle}

                                        </h4>


                                        <div className="react-screening-meta">

                                            <span>

                                                ◇ {screening.roomName}

                                            </span>

                                            <span

                                                className={
                                                    `react-status ${
                                                        screening.status.toLowerCase()
                                                    }`
                                                }
                                            >

                                                ● {getStatusLabel(
                                                    screening.status
                                                )}

                                            </span>

                                        </div>

                                    </div>


                                    {/* COLLEGAMENTO FILM */}

                                    <a

                                        className="react-film-button"

                                        href={`/films/${screening.filmId}`}
                                    >

                                        Scopri il film ↗

                                    </a>

                                </article>

                            ))}

                        </div>

                    </section>

                )
            )}

        </div>
    );
}


/* =====================================================
   COMPONENTE 4 — STATISTICHE
   ===================================================== */

function ProgramStatistics({ screenings }) {

    const totalScreenings = screenings.length;

    const totalFilms = new Set(

        screenings.map(item => item.filmId)

    ).size;

    const totalRooms = new Set(

        screenings.map(item => item.roomId)

    ).size;


    /* Conteggio proiezioni per giorno */

    const byDayMap = {};

    screenings.forEach(item => {

        byDayMap[item.date] =
            (byDayMap[item.date] || 0) + 1;

    });


    const byDay = Object.entries(byDayMap)

        .sort(([dateA], [dateB]) =>
            dateA.localeCompare(dateB)
        )

        .map(([date, count]) => ({

            name: formatShortDate(date),

            count

        }));


    /* Conteggio proiezioni per sala */

    const byRoomMap = {};

    screenings.forEach(item => {

        const roomName = item.roomName;

        byRoomMap[roomName] =
            (byRoomMap[roomName] || 0) + 1;

    });


    const byRoom = Object.entries(byRoomMap)

        .sort((a, b) => b[1] - a[1])

        .map(([name, count]) => ({

            name,

            count

        }));


    return (

        <section className="react-statistics">


            <div className="react-section-heading">

                <span className="react-eyebrow">

                    ✦ THE FESTIVAL IN NUMBERS

                </span>

                <h2>

                    Il cinema in numeri.

                </h2>

                <p>

                    Statistiche calcolate
                    sulle proiezioni visualizzate.

                </p>

            </div>


            {/* CONTATORI */}

            <div className="react-stats-grid">

                <div className="react-stat-card">

                    <span>

                        PROIEZIONI

                    </span>

                    <strong>

                        {totalScreenings}

                    </strong>

                </div>


                <div className="react-stat-card">

                    <span>

                        FILM

                    </span>

                    <strong>

                        {totalFilms}

                    </strong>

                </div>


                <div className="react-stat-card">

                    <span>

                        SALE

                    </span>

                    <strong>

                        {totalRooms}

                    </strong>

                </div>

            </div>


            {/* GRAFICI */}

            <div className="react-charts-grid">


                {/* GRAFICO PER GIORNO */}

                <div className="react-chart-card">

                    <span className="react-eyebrow">

                        ✦ DAILY SCREENINGS

                    </span>

                    <h3>

                        Proiezioni per giorno

                    </h3>


                    {byDay.length > 0 ? (

                        <div className="react-chart-container">

                            <ResponsiveContainer
                                width="100%"
                                height={250}
                            >

                                <BarChart data={byDay}>

                                    <CartesianGrid
                                        stroke="#49313d"
                                        strokeDasharray="3 3"
                                        vertical={false}
                                    />

                                    <XAxis
                                        dataKey="name"
                                        tick={{
                                            fill: "#baadb0",
                                            fontSize: 11
                                        }}
                                        axisLine={false}
                                        tickLine={false}
                                    />

                                    <YAxis
                                        allowDecimals={false}
                                        tick={{
                                            fill: "#baadb0",
                                            fontSize: 11
                                        }}
                                        axisLine={false}
                                        tickLine={false}
                                    />

                                    <Tooltip
                                        contentStyle={{
                                            background: "#24151e",
                                            border: "1px solid #d5b270",
                                            color: "#f6efe5"
                                        }}
                                    />

                                    <Bar
                                        dataKey="count"
                                        name="Proiezioni"
                                        fill="#d5b270"
                                        radius={[5, 5, 0, 0]}
                                    />

                                </BarChart>

                            </ResponsiveContainer>

                        </div>

                    ) : (

                        <p className="react-chart-empty">

                            Nessun dato disponibile.

                        </p>

                    )}

                </div>


                {/* GRAFICO PER SALA */}

                <div className="react-chart-card">

                    <span className="react-eyebrow">

                        ✦ CINEMA VENUES

                    </span>

                    <h3>

                        Proiezioni per sala

                    </h3>


                    {byRoom.length > 0 ? (

                        <div className="react-chart-container">

                            <ResponsiveContainer
                                width="100%"
                                height={250}
                            >

                                <BarChart
                                    data={byRoom}
                                    layout="vertical"
                                    margin={{
                                        left: 5,
                                        right: 15
                                    }}
                                >

                                    <CartesianGrid
                                        stroke="#49313d"
                                        strokeDasharray="3 3"
                                        horizontal={false}
                                    />

                                    <XAxis
                                        type="number"
                                        allowDecimals={false}
                                        tick={{
                                            fill: "#baadb0",
                                            fontSize: 11
                                        }}
                                        axisLine={false}
                                        tickLine={false}
                                    />

                                    <YAxis
                                        type="category"
                                        dataKey="name"
                                        width={105}
                                        tick={{
                                            fill: "#baadb0",
                                            fontSize: 10
                                        }}
                                        axisLine={false}
                                        tickLine={false}
                                    />

                                    <Tooltip
                                        contentStyle={{
                                            background: "#24151e",
                                            border: "1px solid #d5b270",
                                            color: "#f6efe5"
                                        }}
                                    />

                                    <Bar
                                        dataKey="count"
                                        name="Proiezioni"
                                        fill="#823049"
                                        radius={[0, 5, 5, 0]}
                                    />

                                </BarChart>

                            </ResponsiveContainer>

                        </div>

                    ) : (

                        <p className="react-chart-empty">

                            Nessun dato disponibile.

                        </p>

                    )}

                </div>

            </div>

        </section>
    );
}


/* =====================================================
   COMPONENTE PRINCIPALE — APP
   ===================================================== */

export default function App() {


    /* FESTIVAL */

    const [festivals, setFestivals] = useState([]);

    const [festivalId, setFestivalId] = useState(

        new URLSearchParams(
            window.location.search
        ).get("festivalId") || ""

    );


    /* PROIEZIONI */

    const [screenings, setScreenings] = useState([]);


    /* FILTRI */

    const [search, setSearch] = useState("");

    const [date, setDate] = useState("");

    const [roomId, setRoomId] = useState("");

    const [showCancelled, setShowCancelled] = useState(false);


    /* STATO CARICAMENTO */

    const [loading, setLoading] = useState(false);

    const [error, setError] = useState("");

    const [festivalLoading, setFestivalLoading] = useState(true);


    /* =================================================
       RECUPERO FESTIVAL
       ================================================= */

    useEffect(() => {

        const controller = new AbortController();

        async function loadFestivals() {

            try {

                const response = await fetch(
                    "/api/festivals",
                    {
                        signal: controller.signal
                    }
                );

                if (!response.ok) {

                    throw new Error(
                        "Impossibile recuperare i festival."
                    );
                }

                const data = await response.json();

                if (controller.signal.aborted) {
                    return;
                }

                setFestivals(data);


                /* Selezioniamo il festival iniziale */

                setFestivalId(currentId => {

                    const exists = data.some(
                        festival =>
                            String(festival.id) === String(currentId)
                    );

                    if (exists) {

                        return String(currentId);
                    }

                    return data.length > 0

                        ? String(data[0].id)

                        : "";

                });

            } catch (err) {

                if (err.name !== "AbortError") {

                    setError(err.message);
                }

            } finally {

                if (!controller.signal.aborted) {

                    setFestivalLoading(false);
                }
            }
        }

        loadFestivals();

        return () => controller.abort();

    }, []);


    /* =================================================
       RECUPERO PROIEZIONI
       ================================================= */

    useEffect(() => {

        if (!festivalId) {

            setScreenings([]);

            return;
        }

        const controller = new AbortController();

        async function loadScreenings() {

            setLoading(true);

            setError("");

            setScreenings([]);

            try {

                const response = await fetch(

                    `/api/festivals/${festivalId}/screenings`,

                    {
                        signal: controller.signal
                    }
                );

                if (!response.ok) {

                    throw new Error(
                        "Impossibile caricare il programma."
                    );
                }

                const data = await response.json();

                if (!controller.signal.aborted) {

                    setScreenings(data);
                }

            } catch (err) {

                if (err.name !== "AbortError") {

                    setError(err.message);
                }

            } finally {

                if (!controller.signal.aborted) {

                    setLoading(false);
                }
            }
        }

        loadScreenings();

        return () => controller.abort();

    }, [festivalId]);


    /* =================================================
       CAMBIO FESTIVAL
       ================================================= */

    function changeFestival(newId) {

        setFestivalId(newId);

        resetFilters();

        const url = new URL(window.location.href);

        url.searchParams.set("festivalId", newId);

        window.history.replaceState(
            null,
            "",
            url
        );
    }


    /* =================================================
       AZZERA FILTRI
       ================================================= */

    function resetFilters() {

        setSearch("");

        setDate("");

        setRoomId("");

        setShowCancelled(false);
    }


    /* =================================================
       APPLICAZIONE FILTRI
       ================================================= */

    const filteredScreenings = useMemo(() => {

        return screenings

            .filter(screening => {

                const matchesSearch =

                    screening.filmTitle

                        .toLowerCase()

                        .includes(
                            search.toLowerCase().trim()
                        );


                const matchesDate =

                    !date ||

                    screening.date === date;


                const matchesRoom =

                    !roomId ||

                    String(screening.roomId) === roomId;


                const matchesStatus =

                    showCancelled ||

                    screening.status !== "CANCELLED";


                return (
                    matchesSearch &&
                    matchesDate &&
                    matchesRoom &&
                    matchesStatus
                );

            })

            .sort((a, b) => {

                const first = `${a.date}T${a.time}`;

                const second = `${b.date}T${b.time}`;

                return first.localeCompare(second);
            });

    }, [
        screenings,
        search,
        date,
        roomId,
        showCancelled
    ]);


    /* =================================================
       FESTIVAL SELEZIONATO
       ================================================= */

    const selectedFestival = festivals.find(

        festival =>
            String(festival.id) === String(festivalId)

    );


    /* =================================================
       INTERFACCIA
       ================================================= */

    return (

        <div className="react-app">


            {/* NAVBAR */}

            <header className="react-navbar">

                <div className="react-container react-nav-inner">


                    <a
                        href="/"
                        className="react-brand"
                    >

                        ✦ SIW<span>CINE</span>FESTIVAL

                    </a>


                    <nav className="react-nav-links">

                        <a href="/">

                            Home

                        </a>

                        <a href="/festivals">

                            Festival

                        </a>

                        <a
                            href="/react-program/index.html"
                            className="active"
                        >

                            Programma

                        </a>

                    </nav>

                </div>

            </header>



            {/* HERO */}

            <section className="react-hero">

                <div className="react-container">


                    <span className="react-eyebrow">

                        ✦ THE OFFICIAL SCREENING ROOM

                    </span>


                    <h1>

                        Il grande cinema

                        <em>

                            ti aspetta.

                        </em>

                    </h1>


                    <p>

                        Scopri i film, esplora le giornate
                        e lasciati guidare tra
                        le proiezioni dei festival.

                    </p>


                    <div className="react-hero-line">

                        ✦ ✦ ✦

                    </div>

                </div>

            </section>



            {/* CONTENUTO */}

            <main className="react-main">

                <div className="react-container">


                    {/* SELEZIONE FESTIVAL */}

                    {festivals.length > 0 && (

                        <div className="react-festival-panel">


                            <FestivalSelector

                                festivals={festivals}

                                festivalId={festivalId}

                                onChange={changeFestival}

                            />


                            {selectedFestival && (

                                <div className="react-selected-festival">

                                    <span className="react-eyebrow">

                                        OFFICIAL FESTIVAL

                                    </span>


                                    <h2>

                                        {selectedFestival.name}

                                    </h2>


                                    <p>

                                        {selectedFestival.city}

                                        {" ✦ "}

                                        {selectedFestival.year}

                                    </p>

                                </div>

                            )}

                        </div>

                    )}



                    {/* ERRORI */}

                    {error && (

                        <div className="react-error">

                            {error}

                        </div>

                    )}



                    {/* CARICAMENTO */}

                    {(loading || festivalLoading) && (

                        <div className="react-loading">

                            ✦ Caricamento del programma...

                        </div>

                    )}



                    {/* NESSUN FESTIVAL */}

                    {!festivalLoading &&
                     festivals.length === 0 &&
                     !error && (

                        <div className="react-empty">

                            <h3>

                                Nessun festival disponibile.

                            </h3>

                        </div>

                    )}



                    {/* PROGRAMMA */}

                    {!loading &&
                     !festivalLoading &&
                     !error &&
                     festivals.length > 0 && (

                        <>


                            {/* FILTRI */}

                            <ProgramFilters

                                screenings={screenings}

                                search={search}
                                setSearch={setSearch}

                                date={date}
                                setDate={setDate}

                                roomId={roomId}
                                setRoomId={setRoomId}

                                showCancelled={showCancelled}
                                setShowCancelled={setShowCancelled}

                                onReset={resetFilters}

                            />



                            {/* INTESTAZIONE */}

                            <div className="react-section-heading">

                                <span className="react-eyebrow">

                                    ✦ THE FILM PROGRAM

                                </span>


                                <h2>

                                    Il programma ufficiale.

                                </h2>


                                <p>

                                    {filteredScreenings.length}

                                    {" "}

                                    proiezioni visualizzate

                                </p>

                            </div>



                            {/* TIMELINE */}

                            <ScreeningTimeline

                                screenings={filteredScreenings}

                            />



                            {/* STATISTICHE */}

                            <ProgramStatistics

                                screenings={filteredScreenings}

                            />

                        </>

                    )}

                </div>

            </main>



            {/* FOOTER */}

            <footer className="react-footer">

                <div className="react-container">


                    <strong>

                        ✦ SiwCineFestival

                    </strong>


                    <p>

                        THE ART OF CINEMA

                    </p>

                </div>

            </footer>

        </div>
    );
}