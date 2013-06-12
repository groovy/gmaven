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
  private String whitespace(final String text) {
    return "\n\t    " + text + "\n\t    ";
  }

  private void assertUrl(final String url) throws Exception {
    ClassSource source = ClassSource.create(url);
    log(source);

    assertThat(source, notNullValue());
    assertThat(source.url, notNullValue());
    assertThat(source.file, nullValue());
    assertThat(source.inline, nullValue());

    assertThat(source.url, is(new URL(url)));
  }

  @Test
  public void create_url() throws Exception {
    assertUrl("http://google.com");
  }

  @Test
  public void create_url_withWhitespace() throws Exception {
    assertUrl(whitespace("http://google.com"));
  }

  private void assertFile(final String path) throws Exception {
    ClassSource source = ClassSource.create(path);
    log(source);

    assertThat(source, notNullValue());
    assertThat(source.url, nullValue());
    assertThat(source.file, notNullValue());
    assertThat(source.inline, nullValue());

    assertThat(source.file, is(new File(path.trim())));
  }

  @Test
  public void create_file() throws Exception {
    assertFile(util.createTempFile().getPath());
  }

  @Test
  public void create_file_withWhitespace() throws Exception {
    assertFile(whitespace(util.createTempFile().getPath()));
  }

  private void assertInline(final String script) throws Exception {
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

  @Test
  public void create_inline() throws Exception {
    assertInline("println 1234");
  }

  @Test
  public void create_inline_withWhitespace() throws Exception {
    assertInline(whitespace("println 1234"));
  }

  @Test
  public void create_inlineIncrementsCounter() {
    ClassSource.scriptCounter.set(0);

    ClassSource.create("println 1234");
    assertThat(ClassSource.scriptCounter.get(), is(1));

    ClassSource.create("println 5678");
    assertThat(ClassSource.scriptCounter.get(), is(2));
  }
}
