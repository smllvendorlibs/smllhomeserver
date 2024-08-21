# Use an official Arch Linux base image
FROM archlinux:latest

RUN pacman -Syu --noconfirm \
    && pacman -S --noconfirm jre-openjdk git vim

# Clone the repository and set the working directory
RUN cd / && \
    git clone https://github.com/smllvendorlibs/smllhomeserver.git && \
    cd smllhomeserver

# Set the working directory
WORKDIR /smllhomeserver

# Expose port 8080
EXPOSE 8080

# Run the Java application
CMD ["java", "-jar", "smllserver-dist.jar", "."]


