package uk.gov.hmcts.reform.sscs.transformers;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.MockitoAnnotations.initMocks;
import static uk.gov.hmcts.reform.sscs.TestDataConstants.*;

import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import uk.gov.hmcts.reform.sscs.bulkscancore.domain.CaseTransformationResponse;
import uk.gov.hmcts.reform.sscs.bulkscancore.domain.ScannedData;
import uk.gov.hmcts.reform.sscs.bulkscancore.domain.ScannedRecord;
import uk.gov.hmcts.reform.sscs.ccd.domain.*;
import uk.gov.hmcts.reform.sscs.json.SscsJsonExtractor;


public class SscsCaseTransformerTest {

    @Mock
    SscsJsonExtractor sscsJsonExtractor;

    @InjectMocks
    SscsCaseTransformer transformer;

    Map<String, Object> ocrMap = new HashMap<>();

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void givenKeyValuePairsWithPerson1_thenBuildAnAppealWithAppellant() {

        Map<String, Object> pairs = ImmutableMap.<String, Object>builder()
            .put("benefit_type_description", BENEFIT_TYPE_DESCRIPTION)
            .put("person1_title", APPELLANT_TITLE)
            .put("person1_first_name", APPELLANT_FIRST_NAME)
            .put("person1_last_name", APPELLANT_LAST_NAME)
            .put("person1_address_line1", APPELLANT_ADDRESS_LINE1)
            .put("person1_address_line2", APPELLANT_ADDRESS_LINE2)
            .put("person1_address_line3", APPELLANT_ADDRESS_LINE3)
            .put("person1_address_line4", APPELLANT_ADDRESS_LINE4)
            .put("person1_postcode", APPELLANT_POSTCODE)
            .put("person1_phone", APPELLANT_PHONE)
            .put("person1_mobile", APPELLANT_MOBILE)
            .put("person1_date_of_birth", APPELLANT_DATE_OF_BIRTH)
            .put("person1_ni_number", APPELLANT_NI_NUMBER)
            .put("representative_company", REPRESENTATIVE_NAME)
            .put("representative_address_line1", REPRESENTATIVE_ADDRESS_LINE1)
            .put("representative_address_line2", REPRESENTATIVE_ADDRESS_LINE2)
            .put("representative_address_line3", REPRESENTATIVE_ADDRESS_LINE3)
            .put("representative_address_line4", REPRESENTATIVE_ADDRESS_LINE4)
            .put("representative_postcode", REPRESENTATIVE_POSTCODE)
            .put("representative_phone", REPRESENTATIVE_PHONE_NUMBER)
            .put("representative_title", REPRESENTATIVE_PERSON_TITLE)
            .put("representative_first_name", REPRESENTATIVE_PERSON_FIRST_NAME)
            .put("representative_last_name", REPRESENTATIVE_PERSON_LAST_NAME)
            .put("appeal_late_reason", APPEAL_LATE_REASON)
            .put("is_hearing_type_oral", IS_HEARING_TYPE_ORAL)
            .put("is_hearing_type_paper", IS_HEARING_TYPE_PAPER)
            .put("hearing_options_exclude_dates", HEARING_OPTIONS_EXCLUDE_DATES)
            .put("hearing_support_arrangements", HEARING_SUPPORT_ARRANGEMENTS)
            .put("hearing_options_language", HEARING_OPTIONS_LANGUAGE)
            .put("agree_less_hearing_notice_yes", AGREE_LESS_HEARING_NOTICE_YES)
            .put("agree_less_hearing_notice_no", AGREE_LESS_HEARING_NOTICE_NO)
            .put("signature_appellant_name", SIGNATURE_APPELLANT_NAME)
            .build();

        given(sscsJsonExtractor.extractJson(ocrMap)).willReturn(ScannedData.builder().ocrCaseData(pairs).build());

        CaseTransformationResponse result = transformer.transformExceptionRecordToCase(ocrMap);

        assertEquals(buildTestAppealData(), result.getTransformedCase().get("appeal"));

        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    public void givenKeyValuePairsWithPerson2AndPerson1_thenBuildAnAppealWithAppellantAndAppointee() {

        Map<String, Object> pairs = ImmutableMap.<String, Object>builder()
            .put("person1_title", APPOINTEE_TITLE)
            .put("person1_first_name", APPOINTEE_FIRST_NAME)
            .put("person1_last_name", APPOINTEE_LAST_NAME)
            .put("person1_address_line1", APPOINTEE_ADDRESS_LINE1)
            .put("person1_address_line2", APPOINTEE_ADDRESS_LINE2)
            .put("person1_address_line3", APPOINTEE_ADDRESS_LINE3)
            .put("person1_address_line4", APPOINTEE_ADDRESS_LINE4)
            .put("person1_postcode", APPOINTEE_POSTCODE)
            .put("person1_phone", APPOINTEE_PHONE)
            .put("person1_mobile", APPOINTEE_MOBILE)
            .put("person1_date_of_birth", APPOINTEE_DATE_OF_BIRTH)
            .put("person1_ni_number", APPOINTEE_NI_NUMBER)
            .put("person2_title", APPELLANT_TITLE)
            .put("person2_first_name", APPELLANT_FIRST_NAME)
            .put("person2_last_name", APPELLANT_LAST_NAME)
            .put("person2_address_line1", APPELLANT_ADDRESS_LINE1)
            .put("person2_address_line2", APPELLANT_ADDRESS_LINE2)
            .put("person2_address_line3", APPELLANT_ADDRESS_LINE3)
            .put("person2_address_line4", APPELLANT_ADDRESS_LINE4)
            .put("person2_postcode", APPELLANT_POSTCODE)
            .put("person2_date_of_birth", APPELLANT_DATE_OF_BIRTH)
            .put("person2_ni_number", APPELLANT_NI_NUMBER)
            .build();

        given(sscsJsonExtractor.extractJson(ocrMap)).willReturn(ScannedData.builder().ocrCaseData(pairs).build());

        CaseTransformationResponse result = transformer.transformExceptionRecordToCase(ocrMap);

        Name appellantName = Name.builder().title(APPELLANT_TITLE).firstName(APPELLANT_FIRST_NAME).lastName(APPELLANT_LAST_NAME).build();
        Address appellantAddress = Address.builder().line1(APPELLANT_ADDRESS_LINE1).line2(APPELLANT_ADDRESS_LINE2).town(APPELLANT_ADDRESS_LINE3).county(APPELLANT_ADDRESS_LINE4).postcode(APPELLANT_POSTCODE).build();
        Identity appellantIdentity = Identity.builder().nino(APPELLANT_NI_NUMBER).dob("1987-08-12").build();

        Name appointeeName = Name.builder().title(APPOINTEE_TITLE).firstName(APPOINTEE_FIRST_NAME).lastName(APPOINTEE_LAST_NAME).build();
        Address appointeeAddress = Address.builder().line1(APPOINTEE_ADDRESS_LINE1).line2(APPOINTEE_ADDRESS_LINE2).town(APPOINTEE_ADDRESS_LINE3).county(APPOINTEE_ADDRESS_LINE4).postcode(APPOINTEE_POSTCODE).build();
        Identity appointeeIdentity = Identity.builder().nino(APPOINTEE_NI_NUMBER).dob("1990-12-03").build();
        Contact appointeeContact = Contact.builder().phone(APPOINTEE_PHONE).mobile(APPOINTEE_MOBILE).build();
        Appointee appointee = Appointee.builder().name(appointeeName).address(appointeeAddress).contact(appointeeContact).identity(appointeeIdentity).build();

        Appellant expectedAppellant = Appellant.builder().name(appellantName).identity(appellantIdentity).address(appellantAddress).appointee(appointee).build();

        Appellant appellantResult = ((Appeal) result.getTransformedCase().get("appeal")).getAppellant();
        assertEquals(expectedAppellant, appellantResult);

        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    public void givenKeyValuePairsWithPerson2AndNoPerson1_thenBuildAnAppealWithAppellant() {

        Map<String, Object> pairs = ImmutableMap.<String, Object>builder()
            .put("person2_title", APPELLANT_TITLE)
            .put("person2_first_name", APPELLANT_FIRST_NAME)
            .put("person2_last_name", APPELLANT_LAST_NAME)
            .put("person2_address_line1", APPELLANT_ADDRESS_LINE1)
            .put("person2_address_line2", APPELLANT_ADDRESS_LINE2)
            .put("person2_address_line3", APPELLANT_ADDRESS_LINE3)
            .put("person2_address_line4", APPELLANT_ADDRESS_LINE4)
            .put("person2_postcode", APPELLANT_POSTCODE)
            .put("person2_date_of_birth", APPELLANT_DATE_OF_BIRTH)
            .put("person2_ni_number", APPELLANT_NI_NUMBER)
            .build();

        given(sscsJsonExtractor.extractJson(ocrMap)).willReturn(ScannedData.builder().ocrCaseData(pairs).build());

        CaseTransformationResponse result = transformer.transformExceptionRecordToCase(ocrMap);

        Name appellantName = Name.builder().title(APPELLANT_TITLE).firstName(APPELLANT_FIRST_NAME).lastName(APPELLANT_LAST_NAME).build();
        Address appellantAddress = Address.builder().line1(APPELLANT_ADDRESS_LINE1).line2(APPELLANT_ADDRESS_LINE2).town(APPELLANT_ADDRESS_LINE3).county(APPELLANT_ADDRESS_LINE4).postcode(APPELLANT_POSTCODE).build();
        Identity appellantIdentity = Identity.builder().nino(APPELLANT_NI_NUMBER).dob("1987-08-12").build();

        Appellant expectedAppellant = Appellant.builder().name(appellantName).identity(appellantIdentity).address(appellantAddress).build();

        Appellant appellantResult = ((Appeal) result.getTransformedCase().get("appeal")).getAppellant();
        assertEquals(expectedAppellant, appellantResult);

        assertTrue(result.getErrors().isEmpty());
    }


    @Test
    public void givenOnlyOneKeyValuePair_thenBuildAnAppeal() {

        Map<String, Object> pairs = ImmutableMap.<String, Object>builder()
            .put("benefit_type_description", BENEFIT_TYPE_DESCRIPTION).build();

        given(sscsJsonExtractor.extractJson(ocrMap)).willReturn(ScannedData.builder().ocrCaseData(pairs).build());

        CaseTransformationResponse result = transformer.transformExceptionRecordToCase(ocrMap);

        assertEquals(BENEFIT_TYPE_DESCRIPTION, ((Appeal) result.getTransformedCase().get("appeal")).getBenefitType().getCode());

        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    public void givenContradictingPaperAndOralCaseValues_thenAddErrorToList() {
        Map<String, Object> pairs = ImmutableMap.<String, Object>builder()
            .put("is_hearing_type_oral", true)
            .put("is_hearing_type_paper", true).build();

        given(sscsJsonExtractor.extractJson(ocrMap)).willReturn(ScannedData.builder().ocrCaseData(pairs).build());

        CaseTransformationResponse result = transformer.transformExceptionRecordToCase(ocrMap);

        assertTrue(result.getErrors().contains("is_hearing_type_oral and is_hearing_type_paper have contradicting values"));
    }

    @Test
    public void givenBooleanValueIsText_thenAddErrorToList() {
        Map<String, Object> pairs = ImmutableMap.<String, Object>builder()
            .put("is_hearing_type_oral", "I am a text value")
            .put("is_hearing_type_paper", true).build();

        given(sscsJsonExtractor.extractJson(ocrMap)).willReturn(ScannedData.builder().ocrCaseData(pairs).build());

        CaseTransformationResponse result = transformer.transformExceptionRecordToCase(ocrMap);

        assertTrue(result.getErrors().contains("is_hearing_type_oral does not contain a valid boolean value. Needs to be true or false"));
    }

    @Test
    public void givenIsHearingTypeOralBooleanValueIsEmpty_thenIgnoreField() {
        Map<String, Object> pairs = ImmutableMap.<String, Object>builder()
            .put("is_hearing_type_paper", true).build();

        given(sscsJsonExtractor.extractJson(ocrMap)).willReturn(ScannedData.builder().ocrCaseData(pairs).build());

        CaseTransformationResponse result = transformer.transformExceptionRecordToCase(ocrMap);

        assertNull(((Appeal) result.getTransformedCase().get("appeal")).getHearingType());

        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    public void givenAnInvalidDateOfBirth_thenAddErrorToList() {
        Map<String, Object> pairs = ImmutableMap.<String, Object>builder()
            .put("person1_date_of_birth", "12/99/1987").build();

        given(sscsJsonExtractor.extractJson(ocrMap)).willReturn(ScannedData.builder().ocrCaseData(pairs).build());

        CaseTransformationResponse result = transformer.transformExceptionRecordToCase(ocrMap);

        assertTrue(result.getErrors().contains("person1_date_of_birth is an invalid date field. Needs to be in the format dd/mm/yyyy"));
    }

    @Test
    public void givenACaseWithNullOcrData_thenAddErrorToList() {

        given(sscsJsonExtractor.extractJson(ocrMap)).willReturn(ScannedData.builder().ocrCaseData(null).build());

        CaseTransformationResponse result = transformer.transformExceptionRecordToCase(ocrMap);

        assertTrue(result.getErrors().contains("No OCR data, case cannot be created"));
    }

    @Test
    public void givenACaseWithNoOcrData_thenAddErrorToList() {
        Map<String, Object> pairs = ImmutableMap.<String, Object>builder().build();

        given(sscsJsonExtractor.extractJson(ocrMap)).willReturn(ScannedData.builder().ocrCaseData(pairs).build());

        CaseTransformationResponse result = transformer.transformExceptionRecordToCase(ocrMap);

        assertTrue(result.getErrors().contains("No OCR data, case cannot be created"));
    }

    @Test
    public void givenOneDocument_thenBuildACase() {
        Map<String, Object> pairs = ImmutableMap.<String, Object>builder()
            .put("is_hearing_type_paper", true).build();

        List<ScannedRecord> records = new ArrayList<>();
        ScannedRecord scannedRecord = buildTestScannedRecord("http://www.test1.com");
        records.add(scannedRecord);

        given(sscsJsonExtractor.extractJson(ocrMap)).willReturn(ScannedData.builder().ocrCaseData(pairs).records(records).build());

        CaseTransformationResponse result = transformer.transformExceptionRecordToCase(ocrMap);

        @SuppressWarnings("unchecked")
        List<SscsDocument> docs = ((List<SscsDocument>) result.getTransformedCase().get("sscsDocument"));
        assertEquals(scannedRecord.getDocScanDate(), docs.get(0).getValue().getDocumentDateAdded());
        assertEquals(scannedRecord.getFilename(), docs.get(0).getValue().getDocumentFileName());
        assertEquals(scannedRecord.getDocumentLink(), docs.get(0).getValue().getDocumentLink().getDocumentUrl());
        assertEquals("Other document", docs.get(0).getValue().getDocumentType());

        assertTrue(result.getErrors().isEmpty());
    }

    @Test
    public void givenMultipleDocuments_thenBuildACase() {
        Map<String, Object> pairs = ImmutableMap.<String, Object>builder()
            .put("is_hearing_type_paper", true).build();

        List<ScannedRecord> records = new ArrayList<>();
        ScannedRecord scannedRecord1 = buildTestScannedRecord("http://www.test1.com");
        ScannedRecord scannedRecord2 = buildTestScannedRecord("http://www.test2.com");
        records.add(scannedRecord1);
        records.add(scannedRecord2);

        given(sscsJsonExtractor.extractJson(ocrMap)).willReturn(ScannedData.builder().ocrCaseData(pairs).records(records).build());

        CaseTransformationResponse result = transformer.transformExceptionRecordToCase(ocrMap);

        @SuppressWarnings("unchecked")
        List<SscsDocument> docs = ((List<SscsDocument>) result.getTransformedCase().get("sscsDocument"));
        assertEquals(scannedRecord1.getDocScanDate(), docs.get(0).getValue().getDocumentDateAdded());
        assertEquals(scannedRecord1.getFilename(), docs.get(0).getValue().getDocumentFileName());
        assertEquals(scannedRecord1.getDocumentLink(), docs.get(0).getValue().getDocumentLink().getDocumentUrl());
        assertEquals("Other document", docs.get(0).getValue().getDocumentType());
        assertEquals(scannedRecord2.getDocScanDate(), docs.get(1).getValue().getDocumentDateAdded());
        assertEquals(scannedRecord2.getFilename(), docs.get(1).getValue().getDocumentFileName());
        assertEquals(scannedRecord2.getDocumentLink(), docs.get(1).getValue().getDocumentLink().getDocumentUrl());
        assertEquals("Other document", docs.get(1).getValue().getDocumentType());

        assertTrue(result.getErrors().isEmpty());
    }

    private Appeal buildTestAppealData() {
        Name appellantName = Name.builder().title(APPELLANT_TITLE).firstName(APPELLANT_FIRST_NAME).lastName(APPELLANT_LAST_NAME).build();
        Address appellantAddress = Address.builder().line1(APPELLANT_ADDRESS_LINE1).line2(APPELLANT_ADDRESS_LINE2).town(APPELLANT_ADDRESS_LINE3).county(APPELLANT_ADDRESS_LINE4).postcode(APPELLANT_POSTCODE).build();
        Identity appellantIdentity = Identity.builder().nino(APPELLANT_NI_NUMBER).dob("1987-08-12").build();
        Contact appellantContact = Contact.builder().phone(APPELLANT_PHONE).mobile(APPELLANT_MOBILE).build();
        Appellant appellant = Appellant.builder().name(appellantName).identity(appellantIdentity).address(appellantAddress).contact(appellantContact).build();

        Name repName = Name.builder().title(REPRESENTATIVE_PERSON_TITLE).firstName(REPRESENTATIVE_PERSON_FIRST_NAME).lastName(REPRESENTATIVE_PERSON_LAST_NAME).build();
        Address repAddress = Address.builder().line1(REPRESENTATIVE_ADDRESS_LINE1).line2(REPRESENTATIVE_ADDRESS_LINE2).town(REPRESENTATIVE_ADDRESS_LINE3).county(REPRESENTATIVE_ADDRESS_LINE4).postcode(REPRESENTATIVE_POSTCODE).build();
        Contact repContact = Contact.builder().phone(REPRESENTATIVE_PHONE_NUMBER).build();

        ExcludeDate excludeDate = ExcludeDate.builder().value(DateRange.builder().start(HEARING_OPTIONS_EXCLUDE_DATES).build()).build();
        List<ExcludeDate> excludedDates = new ArrayList<>();
        excludedDates.add(excludeDate);

        List<String> hearingSupportArrangements = new ArrayList<>();
        hearingSupportArrangements.add(HEARING_SUPPORT_ARRANGEMENTS);

        return Appeal.builder()
            .benefitType(BenefitType.builder().code(BENEFIT_TYPE_DESCRIPTION).build())
            .appellant(appellant)
            .rep(Representative.builder().hasRepresentative("Yes").name(repName).address(repAddress).contact(repContact).organisation(REPRESENTATIVE_NAME).build())
            .mrnDetails(MrnDetails.builder().mrnLateReason(APPEAL_LATE_REASON).build())
            .hearingType("Oral")
            .hearingOptions(HearingOptions.builder().excludeDates(excludedDates).arrangements(hearingSupportArrangements).languageInterpreter("Yes").languages(HEARING_OPTIONS_LANGUAGE).build())
            .signer(SIGNATURE_APPELLANT_NAME)
        .build();
    }

    private ScannedRecord buildTestScannedRecord(String url) {
        return ScannedRecord.builder()
            .docScanDate("2018-08-10")
            .documentControlNumber("123")
            .documentLink(url)
            .filename("mrn.jpg")
            .documentType("Testing").build();

    }

}
