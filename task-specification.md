# Movie Service

## Objective & Scope
- Store and serve movie data.

## Out of Scope
- Data ingestion / dataset loading.

## API

Endpoints that accept a `limit` param share the same behavior.
- Optional.
- Default: 20.
- Max: 100.

### Get Movie by ID
```
GET /api/movies/{id}
```
- Returns full movie details.
- Returns 404 if not found.

### Batch Get Movies
```
GET /api/movies/batch?ids=
```
- `ids`
  - Required.
  - Comma-separated list of movieIds.
- Returns `List<MovieSummary>` for the given IDs.

### Popular Movies
```
GET /api/movies/popular?limit=
```
- Sorted by `voteCount` descending.
- `limit` follows the shared behavior above.
- Returns `List<MovieSummary>`.

### Latest
```
GET /api/movies/latest?limit=
```
- Sorted by `releaseDate` descending.
- `limit` follows the shared behavior above.
- Returns `List<MovieSummary>`.

### Search Movies
```
GET /api/movies/search?title=&genre=&director=&limit=
```
- At least one of `title`, `genre`, or `director` is required.
  - Returns 400 if none provided.
- Combined via AND logic.
- Partial case-insensitive matches allowed for `title` and `director`.
- `limit` follows the shared behavior above.
- Returns `List<MovieSummary>`.

## Data Model

### DB

**Table: `movie`**

| Field        | Type      |
|--------------|-----------|
| id           | Long      |
| title        | String    |
| voteAverage  | Double    |
| voteCount    | Integer   |
| releaseDate  | LocalDate |
| revenue      | Long      |
| runtime      | Integer   |
| backdropPath | String    |
| budget       | Long      |
| homepage     | String    |
| overview     | String    |
| popularity   | Double    |
| posterPath   | String    |
| genres       | String[]  |
| themes       | String[]  |

### Response Models

**MovieSummary**
- Used by:
  - `search`
  - `popular`
  - `latest`
  - `batch`

| Field       | Type      |
|-------------|-----------|
| id           | Long      |
| title        | String    |
| posterPath   | String    |
| backdropPath | String    |
| voteAverage  | Double    |
| releaseDate  | LocalDate |
| genres       | String[]  |

**MovieDetails**
- Used by `GET /api/movies/{id}`.
- All fields from the table above.
