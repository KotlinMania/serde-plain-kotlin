#if canImport(Testing)
import Testing
import SerdePlain

@Suite("SerdePlain Swift Export Tests")
struct SerdePlainExportTests {
    @Test("SerdePlain swift module imported cleanly")
    func testSwiftModuleLoads() throws {
        #expect(Bool(true), "SerdePlain swift module imported cleanly")
    }
}
#elseif canImport(XCTest)
import XCTest
import SerdePlain

final class SerdePlainExportTests: XCTestCase {
    func testSwiftModuleLoads() throws {
        XCTAssertTrue(true, "SerdePlain swift module imported cleanly")
    }
}
#endif
