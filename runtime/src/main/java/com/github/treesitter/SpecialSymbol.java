package com.github.treesitter;

public enum SpecialSymbol {
  END{
    @Override
    public boolean isError() {
      return false;
    }
  },
  ERROR {
    @Override
    public boolean isError() {
      return true;
    }
  },
  ERROR_REPEATED {
    @Override
    public boolean isError() {
      return true;
    }
  };

    public abstract boolean isError() ;
}
