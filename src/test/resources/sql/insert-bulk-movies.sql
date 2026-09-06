INSERT INTO movie (title, director, vote_average, vote_count, release_date, revenue, runtime, backdrop_path, budget, homepage, overview, popularity, poster_path, genres, themes)
SELECT
    'Bulk Movie ' || i,
    'Bulk Director',
    5.0,
    i,
    DATE '2000-01-01' + i,
    100000,
    100,
    '/bulk_backdrop.jpg',
    100000,
    'https://bulk.example.com',
    'Bulk overview',
    1.0,
    '/bulk_poster.jpg',
    ARRAY['Drama'],
    ARRAY['Bulk']
FROM generate_series(1, 105) AS i;
