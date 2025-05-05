# Genre Chain

Genre Chain is an Android application that lets users explore connections between musical artists based on shared genres. By integrating the MusicBrainz REST API, users can search for artists and follow a chain of genre-based links between them, turning music discovery into a fun, interactive experience.

## Features

- Search for any music artist
- View connected artists by genre
- Track your "genre chain" through an interactive list
- Clean, user-friendly interface built with Material Design
- Real-time data fetched using a public REST API

## API Integration

We used the [MusicBrainz API](https://musicbrainz.org/doc/MusicBrainz_API) to retrieve artist and genre data. All artist information is pulled dynamically using RESTful endpoints.

## Architecture

Genre Chain is built using the MVVM (Model-View-ViewModel) architecture:
- Model: Handles API responses and data classes
- ViewModel: Manages data logic and state
- View: Uses Jetpack Compose to build a responsive UI

## Database

The app uses Room Database to cache searched artists and preserve user history. This improves performance and supports offline access.

## Testing

Tested on:
- Samsung Galaxy S10
- Android Emulator

## Team Members

- Ryan Lucero  
- Forrest Hartley  
- Zach Trusso
