package org.codehaus.gmaven.adapter;

import java.io.File;
import java.net.URL;

import org.sonatype.sisu.litmus.testsupport.TestSupport;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link ClassSource}.
 */
public class ClassSourceTest
    extends TestSupport
{
  @Test
  public void create_fromUrl() throws Exception {
    String url = "http://google.com";
    ClassSource source = ClassSource.create(url);
    log(source);

    assertThat(source, notNullValue());
    assertThat(source.url, notNullValue());
    assertThat(source.file, nullValue());
    assertThat(source.inline, nullValue());

    assertThat(source.url, is(new URL(url)));
  }

  @Test
  public void create_fromFile() throws Exception {
    File file = util.createTempFile();
    ClassSource source = ClassSource.create(file.getPath());
    log(source);

    assertThat(source, notNullValue());
    assertThat(source.url, nullValue());
    assertThat(source.file, notNullValue());
    assertThat(source.inline, nullValue());

    assertThat(source.file, is(file));
  }

  @Test
  public void create_fromInline() throws Exception {
    String script = "println 1234";
    ClassSource source = ClassSource.create(script);
    log(source);

    assertThat(source, notNullValue());
    assertThat(source.url, nullValue());
    assertThat(source.file, nullValue());
    assertThat(source.inline, notNullValue());

    // keep IDEA happy
    assert source.inline != null;

    assertThat(source.inline.name, notNullValue());
    assertThat(source.inline.codeBase, notNullValue());
    assertThat(source.inline.input, notNullValue());
  }
}
