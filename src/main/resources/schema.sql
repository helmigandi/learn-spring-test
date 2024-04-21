CREATE TABLE public.customer
(
    id           uuid NOT NULL,
    phone_number varchar(15) NOT NULL,
    "name"       varchar(255) NOT NULL,
    CONSTRAINT customer_pkey PRIMARY KEY (id)
);
