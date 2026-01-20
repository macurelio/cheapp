package com.cheapp.cheapp;

import org.junit.jupiter.api.Test;

class CheappApplicationTests {

	@Test
	void noSpringContextInUnitSuite() {
		// Intencional: los tests de este repo son unitarios con fakes/mocks.
		// Los tests de integración con Postgres/Testcontainers requieren Docker.
	}
}
