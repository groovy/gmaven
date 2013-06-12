package org.codehaus.gmaven.plugin;

import java.io.File;
import java.net.URL;

import org.sonatype.sisu.litmus.testsupport.TestSupport;

import org.codehaus.gmaven.adapter.ClassSource;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Tests for {@link ClassSourceFactory}.
 */
public class ClassSourceFactoryTest
    extends TestSupport
{
  private ClassSourceFactory underTest;

  @Before
  public void setUp() throws Exception {
    underTest = new ClassSourceFactory();
  }

  private String whitespace(final String text) {
    return "\n\t    " + text + "\n\t    ";
  }

  private void assertUrl(final String url) throws Exception {
    ClassSource source = underTest.create(url);
    log(source);

    assertThat(source, notNullValue());
    assertThat(source.getUrl(), notNullValue());
    assertThat(source.getFile(), nullValue());
    assertThat(source.getInline(), nullValue());

    assertThat(source.getUrl(), is(new URL(url)));
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
    ClassSource source = underTest.create(path);
    log(source);

    assertThat(source, notNullValue());
    assertThat(source.getUrl(), nullValue());
    assertThat(source.getFile(), notNullValue());
    assertThat(source.getInline(), nullValue());

    assertThat(source.getFile(), is(new File(path.trim())));
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
    ClassSource source = underTest.create(script);
    log(source);

    assertThat(source, notNullValue());
    assertThat(source.getUrl(), nullValue());
    assertThat(source.getFile(), nullValue());
    assertThat(source.getInline(), notNullValue());

    // keep IDEA happy
    assert source.getInline() != null;

    assertThat(source.getInline().getName(), notNullValue());
    assertThat(source.getInline().getCodeBase(), notNullValue());
    assertThat(source.getInline().getInput(), notNullValue());
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
    ClassSourceFactory.scriptCounter.set(0);

    underTest.create("println 1234");
    assertThat(ClassSourceFactory.scriptCounter.get(), is(1));

    underTest.create("println 5678");
    assertThat(ClassSourceFactory.scriptCounter.get(), is(2));
  }
}
