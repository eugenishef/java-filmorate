DROP TABLE IF EXISTS events;
DROP TABLE IF EXISTS film_director;
DROP TABLE IF EXISTS user_friends;
DROP TABLE IF EXISTS film_userlikes;
DROP TABLE IF EXISTS review_userlikes;
DROP TABLE IF EXISTS review;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS film_genre;
DROP TABLE IF EXISTS film;
DROP TABLE IF EXISTS genre;
DROP TABLE IF EXISTS director;
DROP TABLE IF EXISTS rating;
DROP TABLE IF EXISTS event_type;
DROP TABLE IF EXISTS operation;

CREATE TABLE event_type(id serial NOT NULL,
                        name varchar NOT NULL,
                        CONSTRAINT event_type_pkey PRIMARY KEY (id));

CREATE TABLE operation(id serial NOT NULL,
                       name varchar NOT NULL,
                       CONSTRAINT operation_pkey PRIMARY KEY (id));

CREATE TABLE rating(id serial NOT NULL,
                    name varchar NOT NULL,
                    CONSTRAINT rating_pkey PRIMARY KEY (id));

CREATE TABLE genre(id serial NOT NULL,
                   name varchar NOT NULL,
                   CONSTRAINT genre_pkey PRIMARY KEY (id));



CREATE TABLE film(id serial NOT NULL,
                  name varchar NOT NULL,
                  description varchar NOT NULL,
                  release_date date NOT NULL,
                  duration int4 NOT NULL,
                  rating_id int4 NOT NULL,
                  CONSTRAINT film_pkey PRIMARY KEY (id),
                  CONSTRAINT film_rating_id_fkey FOREIGN KEY (rating_id) REFERENCES rating(id));

CREATE TABLE film_genre(id serial NOT NULL,
                        film_id int4 NOT NULL,
                        genre_id int4 NOT NULL,
                        CONSTRAINT film_genre_pkey PRIMARY KEY (id),
                        CONSTRAINT film_genre_film_id_fkey FOREIGN KEY (film_id) REFERENCES film(id),
                        CONSTRAINT film_genre_genre_id_fkey FOREIGN KEY (genre_id) REFERENCES genre(id));

CREATE TABLE users(id bigserial NOT NULL,
                   email varchar NOT NULL,
                   login varchar NOT NULL,
                   name varchar NOT NULL,
                   birthday date NOT NULL,
                   CONSTRAINT users_pkey PRIMARY KEY (id));

CREATE TABLE film_userlikes(id bigserial NOT NULL,
                            film_id int4 NOT NULL,
                            user_id int8 NOT NULL,
                            CONSTRAINT film_userlikes_pkey PRIMARY KEY (id),
                            CONSTRAINT film_userlikes_film_id_fkey FOREIGN KEY (film_id) REFERENCES film(id),
                            CONSTRAINT film_userlikes_user_id_fkey FOREIGN KEY (user_id) REFERENCES users(id));

CREATE TABLE user_friends(id bigserial NOT NULL,
                          user_id int8 NOT NULL,
                          user_friend_id int8 NOT NULL,
                          CONSTRAINT user_friends_pkey PRIMARY KEY (id),
                          CONSTRAINT user_friends_user_id_fkey FOREIGN KEY (user_id) REFERENCES users(id),
                          CONSTRAINT user_friends_user_friend_id_fkey FOREIGN KEY (user_friend_id) REFERENCES users(id));

CREATE TABLE director(id serial NOT NULL,
                   name varchar NOT NULL,
                   CONSTRAINT director_pkey PRIMARY KEY (id));

CREATE TABLE film_director(id serial NOT NULL,
                        film_id int4 NOT NULL,
                        director_id int4 NOT NULL,
                        CONSTRAINT film_director_pkey PRIMARY KEY (id),
                        CONSTRAINT film_director_film_id_fkey FOREIGN KEY (film_id) REFERENCES film(id),
                        CONSTRAINT film_director_director_id_fkey FOREIGN KEY (director_id) REFERENCES director(id));
                        
CREATE TABLE review(id bigserial NOT NULL,
                    content varchar NOT NULL,
                    is_positive bool NOT NULL,
                    user_id int8 NOT NULL,
                    film_id int4 NOT NULL,
                    CONSTRAINT review_pkey PRIMARY KEY (id),
                    CONSTRAINT review_user_id_fkey FOREIGN KEY (user_id) REFERENCES users(id),
                    CONSTRAINT review_film_id_fkey FOREIGN KEY (film_id) REFERENCES film(id)
);

CREATE TABLE review_userlikes(id bigserial NOT NULL,
                              review_id int8 NOT NULL,
                              user_id int8 NOT NULL,
                              is_useful bool NOT NULL,
                              CONSTRAINT review_userlikes_pkey PRIMARY KEY (id),
                              CONSTRAINT review_userlikes_review_id_fkey FOREIGN KEY (review_id) REFERENCES review(id),
                              CONSTRAINT review_userlikes_user_id_fkey FOREIGN KEY (user_id) REFERENCES users(id));

CREATE TABLE events(id bigserial NOT NULL,
                    user_id bigserial NOT NULL,
                    event_type_id serial NOT NULL,
                    operation_id serial NOT NULL,
                    entity_id bigserial NOT NULL,
                    timestamp timestamp NOT NULL,
                    CONSTRAINT events_pkey PRIMARY KEY (id),
                    CONSTRAINT user_id_fkey FOREIGN KEY (user_id) REFERENCES users(id),
                    CONSTRAINT event_type_id_fkey FOREIGN KEY (event_type_id) REFERENCES event_type(id),
                    CONSTRAINT operation_id_fkey FOREIGN KEY (operation_id) REFERENCES operation(id));


