CREATE TABLE public.resale_flat_price (
    month character varying(100) NOT NULL,
    town character varying(100) NOT NULL,
    flat_type character varying(100) NOT NULL,
    block character varying(100) NOT NULL,
    street_name character varying(100) NOT NULL,
    storey_range character varying(100) NOT NULL,
    floor_area_sqm character varying(100) NOT NULL,
    flat_model character varying(100) NOT NULL,
    lease_commence_date character varying(100) NOT NULL,
    remaining_lease character varying(100) NOT NULL,
    resale_price character varying(100) NOT NULL
);

ALTER TABLE public.resale_flat_price OWNER TO admin;

select count(*) from resale_flat_price;

delete from resale_flat_price;

--drop table public.resale_flat_price;


