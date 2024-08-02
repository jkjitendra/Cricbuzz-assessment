package com.cricbuzz.news.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

class FileStorageServiceTest {

    @InjectMocks
    private FileStorageService fileStorageService;

    @Mock
    private Path fileStorageLocation;

    String uploadDir = "uploads";

    @BeforeEach
    void setUp() throws IOException {

        fileStorageService = new FileStorageService(uploadDir);
        fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(fileStorageLocation);
    }

    @Test
    void test_StoreFile_Success() throws IOException {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "test.txt", "text/plain", "Test content".getBytes());

        String storedFilePath = fileStorageService.storeFile(mockMultipartFile);

        assertNotNull(storedFilePath);
        assertTrue(storedFilePath.endsWith("test.txt"));

        Path targetLocation = fileStorageLocation.resolve("test.txt");
        assertTrue(Files.exists(targetLocation));
        assertEquals("Test content", new String(Files.readAllBytes(targetLocation)));
    }

    @Test
    void test_StoreFile_InvalidFileName_ThrowsException() {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "../test.txt", "text/plain", "Test content".getBytes());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            fileStorageService.storeFile(mockMultipartFile);
        });

        assertEquals("Sorry! Filename contains invalid path sequence ../test.txt", exception.getMessage());
    }

    @Test
    void test_StoreFile_IOException_ThrowsException() {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "test.txt", "text/plain", "Test content".getBytes());

        // Mock the behavior of Files.copy to throw an IOException
        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.copy(any(InputStream.class), any(Path.class), any(StandardCopyOption.class)))
                    .thenThrow(new IOException("Mocked IOException"));

            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                fileStorageService.storeFile(mockMultipartFile);
            });

            assertEquals("Could not store file test.txt. Please try again!", exception.getMessage());
        }
    }

    @Test
    void test_Constructor_IOException_ThrowsRuntimeException() {
        // Mock the behavior of Files.createDirectories to throw an IOException
        try (MockedStatic<Files> mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.createDirectories(any(Path.class))).thenThrow(new IOException("Mocked IOException"));

            RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                new FileStorageService(uploadDir);
            });

            assertEquals("Could not create the directory where the uploaded files will be stored.", exception.getMessage());
        }
    }
}
