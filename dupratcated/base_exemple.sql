--
-- PostgreSQL database dump
--

SET check_function_bodies = false;

SET SESSION AUTHORIZATION 'spenasal';

ALTER TABLE ONLY "".teacher_formula DROP CONSTRAINT teacher_formula_pkey;
ALTER TABLE ONLY "".jury_formula DROP CONSTRAINT jury_formula_pkey;
ALTER TABLE ONLY "".time_stamp DROP CONSTRAINT stamp_pkey;
ALTER TABLE ONLY "".title DROP CONSTRAINT title_pkey;
ALTER TABLE ONLY "".test DROP CONSTRAINT test_pkey;
ALTER TABLE ONLY "".student DROP CONSTRAINT student_pkey;
ALTER TABLE ONLY "".has_mark DROP CONSTRAINT has_mark_pkey;
ALTER TABLE ONLY "".course DROP CONSTRAINT course_pkey;
DROP TABLE "".login_lock;
DROP TABLE "".root_pass;
DROP TABLE "".teacher_formula;
DROP TABLE "".jury_formula;
DROP TABLE "".time_stamp;
DROP TABLE "".title;
DROP TABLE "".test;
DROP TABLE "".student;
DROP TABLE "".has_mark;
DROP TABLE "".course;
SET SESSION AUTHORIZATION 'postgres';

SET SESSION AUTHORIZATION DEFAULT;

DROP PROCEDURAL LANGUAGE plpgsql;
SET SESSION AUTHORIZATION 'postgres';

DROP FUNCTION "".plpgsql_call_handler();
--
-- TOC entry 22 (OID 16556)
-- Name: plpgsql_call_handler(); Type: FUNC PROCEDURAL LANGUAGE; Schema: ; Owner: postgres
--

CREATE FUNCTION plpgsql_call_handler() RETURNS opaque
    AS '$libdir/plpgsql', 'plpgsql_call_handler'
    LANGUAGE "C";


SET SESSION AUTHORIZATION DEFAULT;

--
-- TOC entry 20 (OID 16557)
-- Name: plpgsql; Type: PROCEDURAL LANGUAGE; Schema: ; Owner: 
--

CREATE TRUSTED PROCEDURAL LANGUAGE plpgsql HANDLER plpgsql_call_handler;


--
-- TOC entry 21 (OID 16557)
-- Name: plpgsql; Type: ACL LANGUAGE; Schema: ; Owner: 
--

REVOKE ALL ON LANGUAGE plpgsql FROM PUBLIC;
GRANT ALL ON LANGUAGE plpgsql TO PUBLIC;


SET SESSION AUTHORIZATION 'postgres';

--
-- TOC entry 23 (OID 16556)
-- Name: plpgsql_call_handler(); Type: ACL; Schema: ; Owner: postgres
--

REVOKE ALL ON FUNCTION plpgsql_call_handler() FROM PUBLIC;
REVOKE ALL ON FUNCTION plpgsql_call_handler() FROM postgres;
GRANT ALL ON FUNCTION plpgsql_call_handler() TO PUBLIC;


SET SESSION AUTHORIZATION 'spenasal';

--
-- TOC entry 2 (OID 575125)
-- Name: course; Type: TABLE; Schema: ; Owner: spenasal
--

CREATE TABLE course (
    id_course integer DEFAULT '0' NOT NULL,
    title character varying(30) DEFAULT '' NOT NULL,
    coeff double precision DEFAULT '0' NOT NULL
);


--
-- TOC entry 3 (OID 575130)
-- Name: has_mark; Type: TABLE; Schema: ; Owner: spenasal
--

CREATE TABLE has_mark (
    id_student integer DEFAULT '0' NOT NULL,
    id_test integer DEFAULT '0' NOT NULL,
    mark double precision DEFAULT '0' NOT NULL
);


--
-- TOC entry 4 (OID 575145)
-- Name: student; Type: TABLE; Schema: ; Owner: spenasal
--

CREATE TABLE student (
    id_student integer DEFAULT '0' NOT NULL,
    name character varying(20) DEFAULT '' NOT NULL,
    last_name character varying(50) DEFAULT '' NOT NULL,
    "comment" character varying(255)
);


--
-- TOC entry 5 (OID 575152)
-- Name: test; Type: TABLE; Schema: ; Owner: spenasal
--

CREATE TABLE test (
    id_test integer DEFAULT '0' NOT NULL,
    coeff double precision DEFAULT '0' NOT NULL,
    id_course integer DEFAULT '0' NOT NULL,
    id_title integer DEFAULT '0' NOT NULL
);


--
-- TOC entry 6 (OID 575158)
-- Name: title; Type: TABLE; Schema: ; Owner: spenasal
--

CREATE TABLE title (
    id_title integer DEFAULT '0' NOT NULL,
    id_desc character varying(30) DEFAULT '' NOT NULL
);


--
-- TOC entry 7 (OID 575292)
-- Name: time_stamp; Type: TABLE; Schema: ; Owner: spenasal
--

CREATE TABLE time_stamp (
    table_name character varying(20) DEFAULT '' NOT NULL,
    stamp integer DEFAULT '0' NOT NULL
);


--
-- TOC entry 8 (OID 606178)
-- Name: jury_formula; Type: TABLE; Schema: ; Owner: spenasal
--

CREATE TABLE jury_formula (
    id_formula integer DEFAULT '0' NOT NULL,
    id_title integer DEFAULT '0' NOT NULL,
    expression character varying(255) DEFAULT '' NOT NULL,
    column_index integer DEFAULT '0' NOT NULL
);


--
-- TOC entry 9 (OID 606181)
-- Name: teacher_formula; Type: TABLE; Schema: ; Owner: spenasal
--

CREATE TABLE teacher_formula (
    id_formula integer DEFAULT '0' NOT NULL,
    id_title integer DEFAULT '0' NOT NULL,
    id_course integer DEFAULT '0' NOT NULL,
    expression character varying(255) DEFAULT '' NOT NULL,
    column_index integer DEFAULT '0' NOT NULL
);


--
-- TOC entry 10 (OID 606206)
-- Name: root_pass; Type: TABLE; Schema: ; Owner: spenasal
--

CREATE TABLE root_pass (
    "password" character varying
);


--
-- TOC entry 11 (OID 606211)
-- Name: login_lock; Type: TABLE; Schema: ; Owner: spenasal
--

CREATE TABLE login_lock (
    is_logged character varying
);


--
-- Data for TOC entry 24 (OID 575125)
-- Name: course; Type: TABLE DATA; Schema: ; Owner: spenasal
--

INSERT INTO course VALUES (6, 'Interfaces Graphiques', 3);
INSERT INTO course VALUES (7, 'BD Avancées', 6);
INSERT INTO course VALUES (1, 'Eco - Gestion', 3);
INSERT INTO course VALUES (2, 'C++', 3);
INSERT INTO course VALUES (0, 'Java', 6);


--
-- Data for TOC entry 25 (OID 575130)
-- Name: has_mark; Type: TABLE DATA; Schema: ; Owner: spenasal
--

INSERT INTO has_mark VALUES (0, 0, 10);
INSERT INTO has_mark VALUES (2, 0, 12);
INSERT INTO has_mark VALUES (3, 0, 13);
INSERT INTO has_mark VALUES (0, 2, 14.5);
INSERT INTO has_mark VALUES (1, 2, 15);
INSERT INTO has_mark VALUES (2, 2, 16);
INSERT INTO has_mark VALUES (3, 2, 15);
INSERT INTO has_mark VALUES (1, 0, 9);
INSERT INTO has_mark VALUES (3, 3, 5);
INSERT INTO has_mark VALUES (4, 3, 9);
INSERT INTO has_mark VALUES (2, 3, 12);
INSERT INTO has_mark VALUES (0, 3, 13);
INSERT INTO has_mark VALUES (1, 3, 15);
INSERT INTO has_mark VALUES (3, 4, 14);
INSERT INTO has_mark VALUES (4, 4, 5);
INSERT INTO has_mark VALUES (1, 4, 18);
INSERT INTO has_mark VALUES (2, 4, 10);
INSERT INTO has_mark VALUES (0, 4, 11);
INSERT INTO has_mark VALUES (7, 4, 0);
INSERT INTO has_mark VALUES (7, 0, 0);
INSERT INTO has_mark VALUES (9, 4, 0);
INSERT INTO has_mark VALUES (9, 0, 0);
INSERT INTO has_mark VALUES (10, 0, 0);
INSERT INTO has_mark VALUES (13, 0, 0);
INSERT INTO has_mark VALUES (3, 5, 0);
INSERT INTO has_mark VALUES (13, 5, 0);
INSERT INTO has_mark VALUES (7, 5, 13);
INSERT INTO has_mark VALUES (9, 5, 15);
INSERT INTO has_mark VALUES (8, 5, 17);
INSERT INTO has_mark VALUES (11, 5, 20);
INSERT INTO has_mark VALUES (6, 5, 3);
INSERT INTO has_mark VALUES (10, 5, 8);
INSERT INTO has_mark VALUES (1, 5, 7.5);
INSERT INTO has_mark VALUES (5, 5, 6.8);
INSERT INTO has_mark VALUES (14, 5, 12);
INSERT INTO has_mark VALUES (0, 5, 14);
INSERT INTO has_mark VALUES (2, 5, 14);
INSERT INTO has_mark VALUES (12, 5, 13);
INSERT INTO has_mark VALUES (4, 5, 11.5);
INSERT INTO has_mark VALUES (0, 6, 16.5);
INSERT INTO has_mark VALUES (6, 6, 6);
INSERT INTO has_mark VALUES (11, 6, 8);
INSERT INTO has_mark VALUES (5, 6, 8);
INSERT INTO has_mark VALUES (1, 6, 7);
INSERT INTO has_mark VALUES (10, 6, 7);
INSERT INTO has_mark VALUES (9, 6, 14);
INSERT INTO has_mark VALUES (7, 6, 15);
INSERT INTO has_mark VALUES (2, 6, 16);
INSERT INTO has_mark VALUES (12, 6, 13.5);
INSERT INTO has_mark VALUES (4, 6, 4);
INSERT INTO has_mark VALUES (14, 6, 3);
INSERT INTO has_mark VALUES (8, 6, 2);
INSERT INTO has_mark VALUES (3, 6, 9);
INSERT INTO has_mark VALUES (13, 6, 11);
INSERT INTO has_mark VALUES (14, 3, 1);
INSERT INTO has_mark VALUES (12, 3, 1);
INSERT INTO has_mark VALUES (9, 3, 1);
INSERT INTO has_mark VALUES (7, 3, 1);
INSERT INTO has_mark VALUES (13, 4, 1);
INSERT INTO has_mark VALUES (8, 4, 6);
INSERT INTO has_mark VALUES (10, 4, 8);
INSERT INTO has_mark VALUES (5, 4, 12);
INSERT INTO has_mark VALUES (6, 4, 13);
INSERT INTO has_mark VALUES (11, 4, 6);
INSERT INTO has_mark VALUES (14, 4, 7.5);
INSERT INTO has_mark VALUES (12, 4, 8.6);
INSERT INTO has_mark VALUES (10, 3, 12);
INSERT INTO has_mark VALUES (11, 3, 13);
INSERT INTO has_mark VALUES (13, 3, 8);
INSERT INTO has_mark VALUES (5, 3, 5);
INSERT INTO has_mark VALUES (6, 3, 6);
INSERT INTO has_mark VALUES (8, 3, 10);
INSERT INTO has_mark VALUES (12, 0, 12);
INSERT INTO has_mark VALUES (14, 0, 5);
INSERT INTO has_mark VALUES (11, 0, 7);
INSERT INTO has_mark VALUES (5, 0, 8);
INSERT INTO has_mark VALUES (6, 0, 6);
INSERT INTO has_mark VALUES (4, 0, 5.6);
INSERT INTO has_mark VALUES (8, 0, 7.5);
INSERT INTO has_mark VALUES (7, 7, 1);
INSERT INTO has_mark VALUES (13, 7, 2);
INSERT INTO has_mark VALUES (9, 7, 3);
INSERT INTO has_mark VALUES (3, 7, 4);
INSERT INTO has_mark VALUES (8, 7, 5);
INSERT INTO has_mark VALUES (14, 7, 6);
INSERT INTO has_mark VALUES (10, 7, 7);
INSERT INTO has_mark VALUES (12, 7, 8);
INSERT INTO has_mark VALUES (4, 7, 9);
INSERT INTO has_mark VALUES (1, 7, 10);
INSERT INTO has_mark VALUES (2, 7, 11);
INSERT INTO has_mark VALUES (5, 7, 12);
INSERT INTO has_mark VALUES (11, 7, 13);
INSERT INTO has_mark VALUES (6, 7, 14);
INSERT INTO has_mark VALUES (0, 7, 15);
INSERT INTO has_mark VALUES (0, 8, 1);
INSERT INTO has_mark VALUES (6, 8, 2);
INSERT INTO has_mark VALUES (11, 8, 3);
INSERT INTO has_mark VALUES (5, 8, 4);
INSERT INTO has_mark VALUES (2, 8, 5);
INSERT INTO has_mark VALUES (1, 8, 6);
INSERT INTO has_mark VALUES (4, 8, 7);
INSERT INTO has_mark VALUES (12, 8, 8);
INSERT INTO has_mark VALUES (10, 8, 9);
INSERT INTO has_mark VALUES (14, 8, 10);
INSERT INTO has_mark VALUES (8, 8, 11);
INSERT INTO has_mark VALUES (3, 8, 12);
INSERT INTO has_mark VALUES (9, 8, 13);
INSERT INTO has_mark VALUES (13, 8, 14);
INSERT INTO has_mark VALUES (7, 8, 15);
INSERT INTO has_mark VALUES (4, 9, 0);
INSERT INTO has_mark VALUES (8, 9, 0);
INSERT INTO has_mark VALUES (11, 9, 0);
INSERT INTO has_mark VALUES (3, 9, 0);
INSERT INTO has_mark VALUES (7, 9, 0);
INSERT INTO has_mark VALUES (12, 9, 0);
INSERT INTO has_mark VALUES (2, 9, 0);
INSERT INTO has_mark VALUES (13, 9, 0);
INSERT INTO has_mark VALUES (9, 9, 0);
INSERT INTO has_mark VALUES (6, 9, 0);
INSERT INTO has_mark VALUES (1, 9, 0);
INSERT INTO has_mark VALUES (14, 9, 0);
INSERT INTO has_mark VALUES (10, 9, 0);
INSERT INTO has_mark VALUES (5, 9, 0);
INSERT INTO has_mark VALUES (0, 9, 0);
INSERT INTO has_mark VALUES (13, 2, 6);
INSERT INTO has_mark VALUES (7, 2, 6);
INSERT INTO has_mark VALUES (8, 2, 5);
INSERT INTO has_mark VALUES (12, 2, 8);
INSERT INTO has_mark VALUES (5, 2, 9);
INSERT INTO has_mark VALUES (11, 2, 9);
INSERT INTO has_mark VALUES (6, 2, 10);
INSERT INTO has_mark VALUES (4, 2, 8);
INSERT INTO has_mark VALUES (14, 2, 7);
INSERT INTO has_mark VALUES (9, 2, 5.6);
INSERT INTO has_mark VALUES (10, 2, 16);


--
-- Data for TOC entry 26 (OID 575145)
-- Name: student; Type: TABLE DATA; Schema: ; Owner: spenasal
--

INSERT INTO student VALUES (0, 'Fabien', 'Vallee', 'sale malgacho !');
INSERT INTO student VALUES (2, 'Nathalie', 'Persin', 'sale skimo !');
INSERT INTO student VALUES (3, 'Laurent', 'Garcia', 'sale homo !');
INSERT INTO student VALUES (4, 'Charles', 'Moulhaud', 'sale colombiano !');
INSERT INTO student VALUES (5, 'Fabien', 'Rozier', NULL);
INSERT INTO student VALUES (6, 'Vivi', 'Soulith', NULL);
INSERT INTO student VALUES (7, 'Sabrina', 'Aoudache', NULL);
INSERT INTO student VALUES (8, 'Mickey', 'Gaudin', NULL);
INSERT INTO student VALUES (9, 'Jean', 'Béguec', NULL);
INSERT INTO student VALUES (10, 'Alexis', 'Lebeuvant', NULL);
INSERT INTO student VALUES (11, 'Julien', 'Smadja', NULL);
INSERT INTO student VALUES (12, 'Julien', 'Moisson', NULL);
INSERT INTO student VALUES (13, 'Philippe', 'Bourgier', NULL);
INSERT INTO student VALUES (14, 'Victor', 'Keophila', NULL);
INSERT INTO student VALUES (1, 'Sebastian', 'Penasal', 'sale colombiano !');


--
-- Data for TOC entry 27 (OID 575152)
-- Name: test; Type: TABLE DATA; Schema: ; Owner: spenasal
--

INSERT INTO test VALUES (0, 1, 1, 1);
INSERT INTO test VALUES (2, 0.5, 0, 1);
INSERT INTO test VALUES (3, 0.5, 2, 14);
INSERT INTO test VALUES (4, 0.5, 2, 15);
INSERT INTO test VALUES (5, 0.4, 7, 17);
INSERT INTO test VALUES (6, 0.6, 7, 1);
INSERT INTO test VALUES (7, 0.8, 6, 1);
INSERT INTO test VALUES (8, 0.5, 6, 18);
INSERT INTO test VALUES (9, 1, 0, 19);


--
-- Data for TOC entry 28 (OID 575158)
-- Name: title; Type: TABLE DATA; Schema: ; Owner: spenasal
--

INSERT INTO title VALUES (0, 'projet');
INSERT INTO title VALUES (1, 'examen');
INSERT INTO title VALUES (2, 'partiel');
INSERT INTO title VALUES (3, 'slip');
INSERT INTO title VALUES (4, 'boulouboulou');
INSERT INTO title VALUES (5, 'ta race en slip !');
INSERT INTO title VALUES (6, 'ta race en slip projet');
INSERT INTO title VALUES (7, 'susmab');
INSERT INTO title VALUES (8, 'wesh les gays !');
INSERT INTO title VALUES (9, 'tp noté 1');
INSERT INTO title VALUES (10, 'Eco & C');
INSERT INTO title VALUES (11, 'maximus');
INSERT INTO title VALUES (12, 'Mario DS');
INSERT INTO title VALUES (13, 'Pour vic');
INSERT INTO title VALUES (14, 'tp 1');
INSERT INTO title VALUES (15, 'tp 2');
INSERT INTO title VALUES (16, 'test max');
INSERT INTO title VALUES (17, 'Projet');
INSERT INTO title VALUES (18, 'RSSFeeder');
INSERT INTO title VALUES (19, 'PLO');


--
-- Data for TOC entry 29 (OID 575292)
-- Name: time_stamp; Type: TABLE DATA; Schema: ; Owner: spenasal
--

INSERT INTO time_stamp VALUES ('teacher_formula', 15);
INSERT INTO time_stamp VALUES ('student', 21);
INSERT INTO time_stamp VALUES ('test', 18);
INSERT INTO time_stamp VALUES ('has_mark', 152);
INSERT INTO time_stamp VALUES ('jury_formula', 15);
INSERT INTO time_stamp VALUES ('course', 14);


--
-- Data for TOC entry 30 (OID 606178)
-- Name: jury_formula; Type: TABLE DATA; Schema: ; Owner: spenasal
--



--
-- Data for TOC entry 31 (OID 606181)
-- Name: teacher_formula; Type: TABLE DATA; Schema: ; Owner: spenasal
--

INSERT INTO teacher_formula VALUES (0, 16, 2, 'max(${tp 2,tp 1})', 2);


--
-- Data for TOC entry 32 (OID 606206)
-- Name: root_pass; Type: TABLE DATA; Schema: ; Owner: spenasal
--

INSERT INTO root_pass VALUES ('chouma');


--
-- Data for TOC entry 33 (OID 606211)
-- Name: login_lock; Type: TABLE DATA; Schema: ; Owner: spenasal
--



--
-- TOC entry 12 (OID 575127)
-- Name: course_pkey; Type: CONSTRAINT; Schema: ; Owner: spenasal
--

ALTER TABLE ONLY course
    ADD CONSTRAINT course_pkey PRIMARY KEY (id_course);


--
-- TOC entry 13 (OID 575132)
-- Name: has_mark_pkey; Type: CONSTRAINT; Schema: ; Owner: spenasal
--

ALTER TABLE ONLY has_mark
    ADD CONSTRAINT has_mark_pkey PRIMARY KEY (id_student, id_test);


--
-- TOC entry 14 (OID 575147)
-- Name: student_pkey; Type: CONSTRAINT; Schema: ; Owner: spenasal
--

ALTER TABLE ONLY student
    ADD CONSTRAINT student_pkey PRIMARY KEY (id_student);


--
-- TOC entry 15 (OID 575154)
-- Name: test_pkey; Type: CONSTRAINT; Schema: ; Owner: spenasal
--

ALTER TABLE ONLY test
    ADD CONSTRAINT test_pkey PRIMARY KEY (id_test);


--
-- TOC entry 16 (OID 575160)
-- Name: title_pkey; Type: CONSTRAINT; Schema: ; Owner: spenasal
--

ALTER TABLE ONLY title
    ADD CONSTRAINT title_pkey PRIMARY KEY (id_title);


--
-- TOC entry 17 (OID 575294)
-- Name: stamp_pkey; Type: CONSTRAINT; Schema: ; Owner: spenasal
--

ALTER TABLE ONLY time_stamp
    ADD CONSTRAINT stamp_pkey PRIMARY KEY (table_name);


--
-- TOC entry 18 (OID 606180)
-- Name: jury_formula_pkey; Type: CONSTRAINT; Schema: ; Owner: spenasal
--

ALTER TABLE ONLY jury_formula
    ADD CONSTRAINT jury_formula_pkey PRIMARY KEY (id_formula);


--
-- TOC entry 19 (OID 606183)
-- Name: teacher_formula_pkey; Type: CONSTRAINT; Schema: ; Owner: spenasal
--

ALTER TABLE ONLY teacher_formula
    ADD CONSTRAINT teacher_formula_pkey PRIMARY KEY (id_formula);


