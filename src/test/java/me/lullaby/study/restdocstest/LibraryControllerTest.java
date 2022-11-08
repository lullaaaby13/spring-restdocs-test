package me.lullaby.study.restdocstest;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.restdocs.operation.Operation;
import org.springframework.restdocs.operation.OperationRequest;
import org.springframework.restdocs.operation.OperationRequestFactory;
import org.springframework.restdocs.operation.OperationResponse;
import org.springframework.restdocs.operation.preprocess.OperationPreprocessor;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.payload.AbstractFieldsSnippet;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.restdocs.snippet.Attributes;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.beneathPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(RestDocumentationExtension.class)
@SpringBootTest
class LibraryControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    public void beforeEach(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation)
//                            .uris()
//                            .withScheme("http")
//                            .withHost("localhost")
//                            .withPort(8080)
//                        .and()

                            .operationPreprocessors()
                            .withRequestDefaults(
                        removeHeaders( "Host"),
                                    new OperationPreprocessor() {

                                        @Override
                                        public OperationRequest preprocess(OperationRequest request) {
                                            HttpHeaders headers = new HttpHeaders();
                                            headers.putAll(request.getHeaders());
                                            headers.set("Authorization", "Bearer 12345");

                                            return new OperationRequestFactory().create(
                                                    request.getUri(),
                                                    request.getMethod(),
                                                    request.getContent(),
                                                    headers,
                                                    request.getParameters(),
                                                    request.getParts()
                                            );
                                        }

                                        @Override
                                        public OperationResponse preprocess(OperationResponse operationResponse) {
                                            return operationResponse;
                                        }
                                    },

                                    prettyPrint()
                            )
                            .withResponseDefaults(
                                    removeHeaders( "Host"),
                                    prettyPrint()
                            )

                )
                .build();
    }

    @DisplayName("Getting Books")
    @Test
    void getBooks() throws Exception {

        this.mockMvc.perform(MockMvcRequestBuilders.get("/books").accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(
                        document("get-books")
                            .document(
                                    requestHeaders( //요청 헤더 문서화
                                            headerWithName(HttpHeaders.ACCEPT).description(APPLICATION_JSON),
                                            headerWithName(HttpHeaders.AUTHORIZATION).description("Bearer {AccessToken}")
                                    ),
                                    responseFields(
                                            // fieldWithPath("title").type(1234).description("제목"),
                                            fieldWithPath("title").type(STRING).description("제목"),
                                            fieldWithPath("writer").type(STRING).description("저자").optional(),
                                            //fieldWithPath("category").type("Object").description("카테고리"),
                                            subsectionWithPath("category").type("Category").description("카테고리")
                                    ),
                                    responseFields(
                                            beneathPath("category").withSubsectionId("category"),
                                            fieldWithPath("name").type(STRING).description("이름")

                                    )

//                                    requestParts(
//                                            partWithName("multipartFile").description("스터디 커버 이미지"),
//                                            partWithName("requestForm").description("스터디 커버 이미지 변경 폼")
//                                    ),
//                                    requestPartFields(
//                                            "requestForm",
//                                            fieldWithPath("studyId").type(JsonFieldType.NUMBER).description("스터디 ID"),
//                                            fieldWithPath("coverImageId").type(JsonFieldType.NUMBER).description("스터디 커버 이미지 ID").optional()
//                                    ),
                             )
                );
    }

}