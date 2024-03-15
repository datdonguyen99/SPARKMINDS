package net.sparkminds.librarymanagement.controller.admin;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.sparkminds.librarymanagement.config.TestConfig;
import net.sparkminds.librarymanagement.payload.request.BookDto;
import net.sparkminds.librarymanagement.payload.request.LoginDto;
import net.sparkminds.librarymanagement.payload.response.BookResponse;
import net.sparkminds.librarymanagement.repository.AuthorRepository;
import net.sparkminds.librarymanagement.repository.BookRepository;
import net.sparkminds.librarymanagement.repository.PublisherRepository;
import net.sparkminds.librarymanagement.security.JwtTokenProvider;
import net.sparkminds.librarymanagement.service.AuthService;
import net.sparkminds.librarymanagement.util.ExcelToDatabase;
import net.sparkminds.librarymanagement.utils.BookCategory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;

import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@SpringBootTest(classes = {TestConfig.class})
@EnableAutoConfiguration
@AutoConfigureMockMvc
class BookControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ExcelToDatabase excelToDatabase;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private PublisherRepository publisherRepository;

    @Autowired
    private AuthService authService;

    @Autowired
    private BookRepository bookRepository;

    private static final String filePath = "src/test/resources/data/data_test_junit5.xlsx";

    @BeforeEach
    void setUp() throws IOException {
//        excelToDatabase.readExcelFile(filePath, 2, "authors");
//        excelToDatabase.readExcelFile(filePath, 3, "publishers");
//        excelToDatabase.readExcelFile(filePath, 0, "books");
    }

    @AfterEach
    void tearDown() {
//        authorRepository.deleteById(88L);
//        authorRepository.deleteById(99L);
//        publisherRepository.deleteById(88L);
//        publisherRepository.deleteById(99L);
    }

    @Test
    @Order(2)
    void testSearchBook() throws Exception {
        final int numberOfBookExpected = 2;
        LoginDto loginDto = new LoginDto("datdonguyenadmin@gmail.com", "Donguyendat@99", "");
        String accessToken = authService.login(loginDto).getAccessToken();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/admin/books")
                        .header("Authorization", "Bearer " + accessToken)
                        .queryParams(new LinkedMultiValueMap<>() {{
                            add("title.contains", "the");
                            add("authorName.contains", "J.K");
                        }}))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(content);
        JsonNode contentNode = root.get("content");
        List<BookResponse> books = objectMapper.convertValue(contentNode, new TypeReference<>() {
        });

        Assertions.assertEquals(numberOfBookExpected, books.size());
    }

    @Test
    @Order(1)
    void testImportBookByCsv() throws Exception {
        LoginDto loginDto = new LoginDto("datdonguyenadmin@gmail.com", "Donguyendat@99", "");
        String accessToken = authService.login(loginDto).getAccessToken();

        String csvFilePath = "src/test/resources/data/books.csv";
        MockMultipartFile csvFile = new MockMultipartFile(
                "file",
                "books.csv",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                new FileInputStream(csvFilePath));

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/admin/import-books")
                        .file(csvFile)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testAddBook() throws Exception {
        final int numberOfBookExpected = 1;
        String bookTitle = "New Title test 1";
        BookDto bookDto = new BookDto(bookTitle, 6L, 7L, 2024, 50,
                "isbn-dsf-231-hdg-157", new BigDecimal(11), null, BookCategory.ART);

        LoginDto loginDto = new LoginDto("datdonguyenadmin@gmail.com", "Donguyendat@99", "");
        String accessToken = authService.login(loginDto).getAccessToken();

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/admin/books")
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookDto)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andReturn();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/admin/books")
                        .header("Authorization", "Bearer " + accessToken)
                        .queryParam("title.equals", bookTitle))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(content);
        JsonNode contentNode = root.get("content");
        List<BookResponse> books = objectMapper.convertValue(contentNode, new TypeReference<>() {
        });

        Assertions.assertEquals(numberOfBookExpected, books.size());
    }

    @Test
    void testUpdateBook() throws Exception {
        final int numberOfBookExpected = 1;
        String editBookTitle = "Updated Title test 1";
        BookDto updateBookDto = new BookDto(editBookTitle, 6L, 7L, 2024, 50,
                "isbn-dsf-231-hdg-157", new BigDecimal(11), null, BookCategory.ART);

        LoginDto loginDto = new LoginDto("datdonguyenadmin@gmail.com", "Donguyendat@99", "");
        String accessToken = authService.login(loginDto).getAccessToken();

        Long bookId = bookRepository.findByTitle("New Title test 1").get().getId();

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/admin/books/{id}", bookId)
                        .header("Authorization", "Bearer " + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateBookDto)))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/admin/books")
                        .header("Authorization", "Bearer " + accessToken)
                        .queryParam("title.equals", editBookTitle))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(content);
        JsonNode contentNode = root.get("content");
        List<BookResponse> books = objectMapper.convertValue(contentNode, new TypeReference<>() {
        });

        Assertions.assertEquals(numberOfBookExpected, books.size());
    }

    @Test
    void testDeleteBook() throws Exception {
        final int numberOfBookExpected = 1;
        String deleteBookTitle = "Updated Title test 1";

        LoginDto loginDto = new LoginDto("datdonguyenadmin@gmail.com", "Donguyendat@99", "");
        String accessToken = authService.login(loginDto).getAccessToken();

        Long bookId = bookRepository.findByTitle(deleteBookTitle).get().getId();
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/admin/books/{id}", bookId)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/admin/books")
                        .header("Authorization", "Bearer " + accessToken)
                        .queryParam("title.equals", deleteBookTitle))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        JsonNode root = objectMapper.readTree(content);
        JsonNode contentNode = root.get("content");
        List<BookResponse> books = objectMapper.convertValue(contentNode, new TypeReference<>() {
        });

        Assertions.assertEquals(numberOfBookExpected, books.size());
    }

    @Test
    void testUploadImage() throws Exception {
        LoginDto loginDto = new LoginDto("datdonguyenadmin@gmail.com", "Donguyendat@99", "");
        String accessToken = authService.login(loginDto).getAccessToken();
        String bookTitleUploadImg = "Calculus 1";
        Long bookId = bookRepository.findByTitle(bookTitleUploadImg).get().getId();
        String imageFilePath = "src/test/resources/data/logo.png";
        MockMultipartFile imageFile = new MockMultipartFile(
                "image",
                "logo.png",
                MediaType.MULTIPART_FORM_DATA_VALUE,
                new FileInputStream(imageFilePath));

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/admin/books/{bookId}/uploadImage", bookId)
                        .file(imageFile)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
    }

//    @Test
//    void testUpdateImage() throws Exception {
//        LoginDto loginDto = new LoginDto("datdonguyenadmin@gmail.com", "Donguyendat@99", "");
//        String accessToken = authService.login(loginDto).getAccessToken();
//        String bookTitleUploadImg = "Calculus 1";
//        Long bookId = bookRepository.findByTitle(bookTitleUploadImg).get().getId();
//        String imageFilePath = "src/test/resources/data/sample_640×426.jpg";
//        MockMultipartFile imageFile = new MockMultipartFile(
//                "image",
//                "sample_640×426.jpg",
//                MediaType.MULTIPART_FORM_DATA_VALUE,
//                new FileInputStream(imageFilePath));
//
//        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/admin/books/{bookId}/updateImage", bookId)
//                        .file(imageFile)
//                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
//                        .content(Files.readAllBytes(Paths.get(imageFilePath))
//                        .header("Authorization", "Bearer " + accessToken))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andReturn();
//    }

    @Test
    void testDeleteImage() throws Exception {
        LoginDto loginDto = new LoginDto("datdonguyenadmin@gmail.com", "Donguyendat@99", "");
        String accessToken = authService.login(loginDto).getAccessToken();
        String deleteBookTitle = "The Diary of a Young Girl";
        Long bookId = bookRepository.findByTitle(deleteBookTitle).get().getId();

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/admin/books/{id}/deleteImage", bookId)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
    }
}
