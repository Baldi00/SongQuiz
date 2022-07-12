package it.andrea;

public record MyMp3Tag(String title, String artist, String album) {
    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }
}
