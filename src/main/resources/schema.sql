CREATE TABLE IF NOT EXISTS Users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL,
    login VARCHAR(255) NOT NULL,
    name VARCHAR(255),
    birthday DATE,
    UNIQUE(email)
);

CREATE TABLE IF NOT EXISTS Films (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    release_date DATE,
    duration INT,
    mpa_rating VARCHAR(10) DEFAULT 'NR'
);

CREATE TABLE IF NOT EXISTS Genres (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS FilmGenres (
    film_id BIGINT,
    genre_id BIGINT,
    PRIMARY KEY (film_id, genre_id),
    FOREIGN KEY (film_id) REFERENCES Films(id) ON DELETE CASCADE,
    FOREIGN KEY (genre_id) REFERENCES Genres(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Likes (
    user_id BIGINT,
    film_id BIGINT,
    PRIMARY KEY (user_id, film_id),
    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE,
    FOREIGN KEY (film_id) REFERENCES Films(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS Friendships (
    user_id BIGINT,
    friend_id BIGINT,
    status VARCHAR(20),
    PRIMARY KEY (user_id, friend_id),
    FOREIGN KEY (user_id) REFERENCES Users(id) ON DELETE CASCADE,
    FOREIGN KEY (friend_id) REFERENCES Users(id) ON DELETE CASCADE
);
