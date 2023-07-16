/**
 * Defines a Jackson extension for sdx-rest.
 */
module br.pro.hashi.sdx.rest.jackson {
	requires transitive br.pro.hashi.sdx.rest;
	requires transitive com.fasterxml.jackson.databind;

	exports br.pro.hashi.sdx.rest.jackson;
	exports br.pro.hashi.sdx.rest.jackson.client;
	exports br.pro.hashi.sdx.rest.jackson.server;
}