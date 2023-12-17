/*
 * Copyright (c) 2023 Marcelo Hashimoto
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */

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
