/*
 *  Copyright 2016 incode.org
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.incode.module.document.dom.spi;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import javax.activation.MimeType;
import javax.inject.Inject;

import com.google.common.base.Splitter;
import com.google.common.io.ByteSource;
import com.google.common.io.CharSource;
import com.google.common.io.Resources;

import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.services.i18n.TranslatableString;
import org.apache.isis.applib.services.message.MessageService;
import org.apache.isis.applib.value.Blob;
import org.apache.isis.applib.value.Clob;

import org.incode.module.document.dom.impl.docs.Document;

public interface UrlDownloadService {

    @Programmatic
    Blob downloadAsBlob(final Document document);

    @Programmatic
    Clob downloadAsClob(final Document document);

    @DomainService(nature = NatureOfService.DOMAIN)
    public class Default implements UrlDownloadService {

        public String getId() {
            return "incodeDocuments.UrlDownloadService$Default";
        }

        @Programmatic
        public Blob downloadAsBlob(final Document document) {
            try {
                final URL url = new URL(document.getExternalUrl());

                final HttpURLConnection httpConn = openConnection(url);
                if (httpConn == null) {
                    return null;
                }

                final String contentType = httpConn.getContentType();

                final MimeType mimeType = determineMimeType(contentType);
                if (mimeType == null) {
                    return null;
                }

                httpConn.disconnect();

                final ByteSource byteSource = Resources.asByteSource(url);
                final byte[] bytes = byteSource.read();

                return new Blob(document.getName(), mimeType.getBaseType(), bytes);

            } catch (IOException e) {
                messageService.warnUser(TranslatableString.tr(
                        "Could not download from URL"),
                        UrlDownloadService.class, "download");
                return null;
            }
        }


        @Programmatic
        public Clob downloadAsClob(final Document document) {
            try {
                final URL url = new URL(document.getExternalUrl());

                final HttpURLConnection httpConn = openConnection(url);
                if (httpConn == null) {
                    return null;
                }

                final String contentType = httpConn.getContentType();

                final MimeType mimeType = determineMimeType(contentType);
                if (mimeType == null) {
                    return null;
                }

                final Charset charset = determineCharset(contentType);
                if (charset == null) {
                    return null;
                }
                httpConn.disconnect();

                final CharSource charSource = Resources.asCharSource(url, charset);
                final String chars = charSource.read();

                return new Clob(document.getName(), mimeType.getBaseType(), chars);
            } catch (IOException e) {
                messageService.warnUser(TranslatableString.tr(
                        "Could not download from URL"),
                        UrlDownloadService.class, "downloadExternalUrlAsClob");
                return null;
            }
        }

        private HttpURLConnection openConnection(final URL url) throws IOException {
            final HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            final int responseCode = httpConn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                messageService.warnUser(TranslatableString.tr(
                        "Could not download from URL (responseCode: {responseCode})",
                        "responseCode", "" + responseCode),
                        UrlDownloadService.class, "openConnection");
                return null;
            }
            return httpConn;
        }

        private MimeType determineMimeType(final String contentType) {
            final String mimeType = parseMimeType(contentType);
            if(mimeType == null) {
                return null;
            }
            try {
                return new MimeType(mimeType);
            } catch (Exception e) {
                messageService.warnUser(TranslatableString.tr(
                        "Could not download from URL (mime type not recognized: {mimeType})",
                        "mimeType", mimeType),
                        UrlDownloadService.class, "determineMimeType");
                return null;
            }
        }

        // text/plain; charset=UTF-8
        private String parseMimeType(final String contentType) {
            final Iterable<String> values = Splitter.on(";").split(contentType);
            for (String value : values) {
                // is simply the first part
                return value.trim();
            }

            messageService.warnUser(TranslatableString.tr(
                    "Could not download from URL (mime type not recognized within content-type header '{contentType}')",
                    "contentType", contentType),
                    UrlDownloadService.class, "parseMimeType");
            return null;
        }


        private Charset determineCharset(final String contentType) {

            final String charsetName = parseCharset(contentType);
            if(charsetName == null) {
                return null;
            }

            try {
                return Charset.forName(charsetName);
            } catch (Exception e) {
                messageService.warnUser(TranslatableString.tr(
                        "Could not download from URL (charset '{charsetName}' not recognized)",
                        "charsetName", charsetName),
                        UrlDownloadService.class, "determineCharset");
                return null;
            }
        }

        // text/plain; charset=UTF-8
        private String parseCharset(final String contentType) {
            final Iterable<String> values = Splitter.on(";").split(contentType);

            for (String value : values) {
                value = value.trim();

                if (value.toLowerCase().startsWith("charset=")) {
                    return value.substring("charset=".length());
                }
            }

            messageService.warnUser(TranslatableString.tr(
                    "Could not download from URL (charset not recognized within content-type header '{contentType}')",
                    "contentType", contentType),
                    UrlDownloadService.class, "parseCharset");
            return null;
        }

        @Inject
        MessageService messageService;

    }

}
