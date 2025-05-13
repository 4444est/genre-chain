# Genre Chain

[GitHub Repository](https://github.com/4444est/genre-chain)

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
- View: Built with Jetpack Compose for a responsive, modern UI


## Setup and Installation

### Prerequisites:
- Android Studio Flamingo or newer
- Android SDK 33 or higher
- Minimum API level: 26 (Android 8.0)

### To run locally:
1. Clone this repository:
2. Open the project in Android Studio
3. Let Gradle sync
4. Run the app on an emulator or a physical device


## System Requirements

- Android device running Android 8.0 (API 26) or higher
- Minimum 2GB RAM
- Internet connection (to access MusicBrainz API)

## Screenshots
 
Home screen with artist search

![Screenshot 2025-05-13 062258](https://github.com/user-attachments/assets/5d35de7e-1b12-464b-ad73-250d349cfdee)
  
Genre-based artist connection list


![Screenshot 2025-05-13 062537](https://github.com/user-attachments/assets/960adf1b-682a-4adf-95a8-683fd80c2db2)



## Troubleshooting

- Blank artist results: Check if your internet connection is active
- API call fails: MusicBrainz may temporarily rate-limit API requests — try again after a minute
- App won't compile: Make sure your Gradle and SDK versions match the project’s `build.gradle` file

## Contribution Guidelines

This is a student project and is not currently accepting external contributions. For questions or feature requests, contact a team member directly.

## Contact

- Ryan Lucero – rml392@nau.edu  
- Forrest Hartley – fh272@nau.edu
- Zach Trusso – zat35@nau.edu


## Testing

Tested on:
- Samsung Galaxy S10
- Pixel 5 (Android Emulator)
- Android Studio Emulator (API 30, API 33)
