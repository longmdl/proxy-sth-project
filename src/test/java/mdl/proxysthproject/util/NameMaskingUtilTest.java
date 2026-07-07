package mdl.proxysthproject.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NameMaskingUtilTest {

    @Test
    void maskName_NullOrEmpty_ReturnsOriginal() {
        assertNull(NameMaskingUtil.maskName(null));
        assertEquals("", NameMaskingUtil.maskName(""));
        assertEquals("   ", NameMaskingUtil.maskName("   "));
    }

    @Test
    void maskName_SingleWord_ReturnsFully() {
        assertEquals("Nguyen", NameMaskingUtil.maskName("Nguyen"));
    }

    @Test
    void maskName_TwoWords_MasksCorrectly() {
        assertEquals("N* B*", NameMaskingUtil.maskName("Nguyen B"));
        assertEquals("N* B*", NameMaskingUtil.maskName(" Nguyen B "));
    }

    @Test
    void maskName_ThreeOrMoreWords_MasksMiddle() {
        assertEquals("Nguyen *** B", NameMaskingUtil.maskName("Nguyen Van B"));
        assertEquals("Nguyen *** C", NameMaskingUtil.maskName("Nguyen Thi Be C"));
        assertEquals("Nguyen *** E", NameMaskingUtil.maskName("Nguyen A B C D E"));
    }
}
