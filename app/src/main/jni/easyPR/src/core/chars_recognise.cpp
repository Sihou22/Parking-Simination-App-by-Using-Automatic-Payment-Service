#include "../../include/core/chars_recognise.h"
#include "../../include/util/util.h"

namespace easypr {

CCharsRecognise::CCharsRecognise() { m_charsSegment = new CCharsSegment(); }

CCharsRecognise::~CCharsRecognise() { SAFE_RELEASE(m_charsSegment); }

void CCharsRecognise::loadANN(const char* s) {
    CharsIdentify::instance()->loadANN(s);
}

int CCharsRecognise::charsRecognise(Mat plate, std::string& plateLicense) {
  std::vector<Mat> matChars;

  int result = m_charsSegment->charsSegment(plate, matChars);
  if (result == 0) {//如果结果为0，代表7个字符图像获取成功
    for (auto block : matChars) {
      auto character = CharsIdentify::instance()->identify(block);//识别字符图像组
      plateLicense.append(character.second);//将获得的字符拼接到上一个字符之后
    }
  }

  if (plateLicense.size() < 7) {//若获得的字符数小于七则失败
    return -1;
  }

  return result;//成功

}
}